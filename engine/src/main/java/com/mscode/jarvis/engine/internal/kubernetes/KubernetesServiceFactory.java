package com.mscode.jarvis.engine.internal.kubernetes;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceFactory;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.addEnv;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.loadFromYaml;

public class KubernetesServiceFactory implements ServiceFactory {

    private final Path basePath;
    private final KubernetesClient client;

    public KubernetesServiceFactory(Path basePath, KubernetesClient client) {
        this.basePath = basePath;
        this.client = client;
    }

    public Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        String name = deployment.getString("name");

        List<HasMetadata> resources = descriptor.getK8s().stream()
                .flatMap(p -> loadFromYaml(client, basePath.resolve(p)).stream())
                .toList();

        Assert.notEmpty(resources, "Missing resources for " + name + " deployment!");

        Map<String, String> env = mergeEnv(descriptor, deployment);

        resources.stream().map(KubernetesUtils::getPodSpec).filter(Objects::nonNull)
                .flatMap(podSpec -> podSpec.getContainers().stream())
                .forEach(container -> addEnv(container, env));

        return new KubernetesService(client, resources, deployment);
    }

    @Override
    public boolean supports(DeploymentDescriptor descriptor) {
        return !descriptor.getK8s().isEmpty();
    }

    protected static Map<String, String> mergeEnv(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        Map<String, String> env = new HashMap<>(descriptor.getEnv());

        if (deployment.hasNonDefaultValue("env")) {
            String[] envs = deployment.getStringArray("env");
            for (String e : envs) {
                String[] parts = e.split("=");
                Assert.state(parts.length % 2 == 0, "Envs must be defined in 'key=value' format!");
                env.put(parts[0], parts[1]);
            }
        }

        return env;
    }

}
