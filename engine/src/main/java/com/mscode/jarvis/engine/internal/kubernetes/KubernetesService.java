package com.mscode.jarvis.engine.internal.kubernetes;

import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Await;
import com.mscode.jarvis.engine.internal.JarvisService;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.listNonTerminatingPods;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.logs;
import static com.mscode.jarvis.engine.internal.utils.JarvisUtils.waitForDelay;
import static org.springframework.util.CollectionUtils.isEmpty;

public class KubernetesService extends JarvisService {

    private final KubernetesClient client;
    private final List<HasMetadata> resources;

    private List<HasMetadata> created;
    private List<LogWatch> logWatches;

    public KubernetesService(KubernetesClient client, List<HasMetadata> resources, MergedAnnotation<Deployment> annotation) {
        super(annotation);
        this.client = client;
        this.resources = resources;
    }

    @Override
    public Await start() {
        int delayed = getAnnotation().getInt("delayed");

        created = client.resourceList(resources).createOrReplace();

        if (delayed > 0) {
            return (amount, timeUnit) -> waitForDelay(delayed, amount, timeUnit);
        }

        return (amount, timeUnit) -> client.resourceList(created)
                .waitUntilCondition(KubernetesUtils::isReadyOrCompleted, amount, timeUnit);
    }

    @Override
    public boolean stop() {
        if (!isEmpty(logWatches)) {
            logWatches.forEach(LogWatch::close);
        }

        return client.resourceList(created).delete();
    }

    @Override
    public void forwardLogsTo(Path directory) throws IOException {
        Assert.notEmpty(created, "forwardLogsTo(Path) called before start()");

        logWatches = new LinkedList<>();
        for (Pod pod : listNonTerminatingPods(client, created)) {
            for (Container container : pod.getSpec().getContainers()) {
                String filename = pod.getMetadata().getName() + "." + container.getName() + ".log";

                LogWatch logWatch = logs(client, pod, container)
                        .watchLog(Files.newOutputStream(directory.resolve(filename)));

                logWatches.add(logWatch);
            }
        }
    }

}
