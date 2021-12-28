package com.mscode.jarvis.engine.internal.kubernetes;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceFactory;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.convertToJob;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.loadFromYaml;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.override;
import static com.mscode.jarvis.engine.internal.utils.JarvisUtils.mergeEnv;
import static com.mscode.jarvis.engine.internal.utils.JarvisUtils.parsePorts;
import static com.mscode.jarvis.engine.internal.utils.JarvisUtils.parseVolumes;

@Getter
@Order(1)
@Component
public class KubernetesServiceFactory implements ServiceFactory {

    protected final KubernetesClient client;
    protected final KubernetesProperties properties;

    @Autowired
    public KubernetesServiceFactory(KubernetesClient client, KubernetesProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        String name = deployment.getString("name");

        List<HasMetadata> resources = descriptor.getK8s().stream()
                .flatMap(p -> loadFromYaml(client, properties.getBasePath().resolve(p)).stream())
                .toList();

        Assert.notEmpty(resources, "Missing resources for " + name + " deployment!");

        return create(resources, descriptor, deployment);
    }

    public Service create(List<HasMetadata> resources, DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        if (properties.isConvertCronJobToJob()) {
            resources = convertToJob(resources);
        }

        KubernetesOverride values = KubernetesOverride.builder()
                .name(deployment.getString("name"))
                .namespace(properties.getNamespace())
                .env(mergeEnv(descriptor, deployment))
                .volumes(parseVolumes(descriptor))
                .ports(parsePorts(descriptor))
                .build();

        List<HasMetadata> overridden = override(resources, values);

        return new KubernetesService(client, overridden, deployment);
    }

    @Override
    public boolean supports(DeploymentDescriptor descriptor) {
        return !descriptor.getK8s().isEmpty();
    }

}
