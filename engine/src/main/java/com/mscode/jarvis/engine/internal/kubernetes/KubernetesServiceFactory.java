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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.addEnv;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.loadFromYaml;
import static com.mscode.jarvis.engine.internal.utils.JarvisUtils.mergeEnv;

public class KubernetesServiceFactory implements ServiceFactory {

    private final Path basePath;
    private final String namespace;
    private final KubernetesClient client;

    public KubernetesServiceFactory(KubernetesClient client, Path basePath, String namespace) {
        this.client = client;
        this.basePath = basePath;
        this.namespace = namespace;
    }

    public Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        String name = deployment.getString("name");

        List<HasMetadata> resources = descriptor.getK8s().stream()
                .flatMap(p -> loadFromYaml(client, basePath.resolve(p)).stream())
                .peek(r -> r.getMetadata().setNamespace(namespace))
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

}
