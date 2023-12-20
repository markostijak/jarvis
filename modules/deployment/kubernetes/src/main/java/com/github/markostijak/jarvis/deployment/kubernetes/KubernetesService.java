package com.github.markostijak.jarvis.deployment.kubernetes;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Await;
import com.github.markostijak.jarvis.deployment.core.support.AbstractService;
import com.github.markostijak.jarvis.deployment.kubernetes.utils.Kubernetes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import org.springframework.boot.convert.DurationStyle;

public class KubernetesService extends AbstractService {

    private final Path logDirectory;
    private final KubernetesClient client;
    private final List<HasMetadata> resources;

    private final List<LogWatch> logWatches = new ArrayList<>();

    public KubernetesService(KubernetesClient client, List<HasMetadata> resources, Deployment deployment, Path logDirectory) {
        super(deployment);
        this.client = client;
        this.resources = resources;
        this.logDirectory = logDirectory;
    }

    @Override
    public Await start() throws IOException {
        long delayed = DurationStyle.SIMPLE.parse(deployment.delayed()).toMillis();

        List<HasMetadata> created = Kubernetes.apply(client, resources);

        Kubernetes.waitUntilPodIsRunning(client, created, getName());
        for (Pod pod : Kubernetes.listNonTerminatingPods(client, created, getName())) {
            for (Container container : pod.getSpec().getContainers()) {
                String filename = pod.getMetadata().getName() + "." + container.getName() + ".log";

                LogWatch logWatch = Kubernetes.logs(client, pod, container)
                        .watchLog(Files.newOutputStream(logDirectory.resolve(filename)));

                logWatches.add(logWatch);
            }
        }

        if (delayed > 0) {
            return timeout -> Thread.sleep(Math.min(timeout.toMillis(), delayed));
        }

        return timeout -> Kubernetes.waitUntilReady(client, created, timeout);
    }

    @Override
    public boolean stop() {
        Kubernetes.delete(client, resources);
        logWatches.forEach(LogWatch::close);
        logWatches.clear();
        return true;
    }

    @Override
    public boolean isRunning() {
        return !Kubernetes.get(client, resources).isEmpty();
    }

}
