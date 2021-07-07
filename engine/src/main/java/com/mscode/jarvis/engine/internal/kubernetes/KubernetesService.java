package com.mscode.jarvis.engine.internal.kubernetes;

import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Await;
import com.mscode.jarvis.engine.api.Service;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.core.annotation.MergedAnnotation;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class KubernetesService implements Service {

    private final KubernetesClient client;
    private final List<HasMetadata> resources;
    private final MergedAnnotation<Deployment> annotation;

    public KubernetesService(KubernetesClient client, List<HasMetadata> resources, MergedAnnotation<Deployment> annotation) {
        this.client = client;
        this.resources = resources;
        this.annotation = annotation;
    }

    @Override
    public Await start() {
        Integer delayed = Optional.ofNullable(annotation.getMetaSource())
                .flatMap(a -> a.getValue("delayed", Integer.class)).orElse(null);

        if (delayed != null) {
            return (amount, timeUnit) -> {
                client.resourceList(resources).createOrReplace();
                TimeUnit.SECONDS.sleep(Math.min(timeUnit.toSeconds(amount), delayed));
            };
        }

        return (amount, unit) -> client.resourceList(resources).createOrReplaceAnd().waitUntilReady(amount, unit);
    }

    @Override
    public boolean stop() {
        return client.resourceList(resources).delete();
    }

    @Override
    public String getName() {
        return annotation.getString("name");
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
