package com.mscode.jarvis.runner.internal;

import com.mscode.jarvis.runner.DeploymentDescriptor;
import com.mscode.jarvis.runner.annotations.Deployment;
import com.mscode.jarvis.runner.internal.utils.KubernetesUtils;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mscode.jarvis.runner.internal.utils.KubernetesUtils.addEnv;
import static com.mscode.jarvis.runner.internal.utils.KubernetesUtils.getResources;

public class ServiceFactory {

    private final Path basePath;
    private final KubernetesClient client;

    public ServiceFactory(Path basePath, KubernetesClient client) {
        this.basePath = basePath;
        this.client = client;
    }

    public Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        String name = deployment.getString("name");
        MergedAnnotation<?> annotation = deployment.getMetaSource();

        Assert.notNull(annotation, "Annotation can't be null");

        List<HasMetadata> resources = descriptor.getPaths().stream()
                .flatMap(p -> getResources(client, basePath.resolve(p)).stream())
                .toList();

        Assert.notEmpty(resources, "Missing resources for " + name + " deployment!");

        Map<String, String> env = mergeEnv(descriptor, annotation);

        resources.stream().map(KubernetesUtils::getPodSpec).filter(Objects::nonNull)
                .flatMap(podSpec -> podSpec.getContainers().stream())
                .forEach(container -> addEnv(container, env));

        return new Service(name, resources);
    }

    protected static Map<String, String> mergeEnv(DeploymentDescriptor descriptor, MergedAnnotation<?> annotation) {
        Map<String, String> env = new HashMap<>(descriptor.getEnv());

        if (annotation.hasNonDefaultValue("env")) {
            String[] envs = annotation.getStringArray("env");
            for (String e : envs) {
                String[] parts = e.split("=");
                env.put(parts[0], parts[1]);
            }
        }

        return env;
    }

}
