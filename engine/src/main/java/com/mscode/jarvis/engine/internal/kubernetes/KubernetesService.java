package com.mscode.jarvis.engine.internal.kubernetes;

import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Await;
import com.mscode.jarvis.engine.api.Service;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import org.springframework.core.annotation.MergedAnnotation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.listPods;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.logs;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.waitUntilReadyOrCompleted;
import static com.mscode.jarvis.engine.internal.utils.JarvisUtils.waitForDelay;

public class KubernetesService implements Service {

    private final KubernetesClient client;
    private final List<HasMetadata> resources;
    private final MergedAnnotation<Deployment> annotation;
    private final List<LogWatch> logWatches;

    private List<HasMetadata> created;

    public KubernetesService(KubernetesClient client, List<HasMetadata> resources, MergedAnnotation<Deployment> annotation) {
        this.client = client;
        this.resources = resources;
        this.annotation = annotation;
        this.logWatches = new ArrayList<>(3);
    }

    @Override
    public Await start() {
        int delayed = annotation.getInt("delayed");

        created = client.resourceList(resources).createOrReplace();

        if (delayed > 0) {
            return (amount, timeUnit) -> waitForDelay(delayed, amount, timeUnit);
        }

        return (amount, timeUnit) -> waitUntilReadyOrCompleted(client, created, amount, timeUnit);
    }

    @Override
    public boolean stop() {
        logWatches.forEach(LogWatch::close);
        return client.resourceList(created).delete();
    }

    @Override
    public void forwardLogsTo(Path directory) throws IOException {
        for (Pod pod : listPods(client, created)) {
            for (Container container : pod.getSpec().getContainers()) {
                String filename = pod.getMetadata().getName() + "." + container.getName() + ".log";

                LogWatch logWatch = logs(client, pod, container)
                        .watchLog(Files.newOutputStream(directory.resolve(filename)));

                logWatches.add(logWatch);
            }
        }
    }

    @Override
    public String getName() {
        return annotation.getString("name");
    }

    @Override
    public int getOrder() {
        return annotation.getInt("order");
    }

    @Override
    public MergedAnnotation<Deployment> getAnnotation() {
        return annotation;
    }

    @Override
    public String toString() {
        return "KubernetesService{" +
                "name=" + getName() +
                '}';
    }

}
