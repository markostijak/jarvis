package com.mscode.jarvis.engine.internal.kubernetes;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceFactory;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.addEnv;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.convertToJob;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.loadFromYaml;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.replacePorts;
import static com.mscode.jarvis.engine.internal.utils.JarvisUtils.mergeEnv;
import static com.mscode.jarvis.engine.internal.utils.JarvisUtils.parsePorts;

@Order(1)
@Component
public class KubernetesServiceFactory implements ServiceFactory {

    private final KubernetesClient client;
    private final KubernetesProperties properties;

    @Autowired
    public KubernetesServiceFactory(KubernetesClient client, KubernetesProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    public Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        String name = deployment.getString("name");

        List<HasMetadata> resources = descriptor.getK8s().stream()
                .flatMap(p -> loadFromYaml(client, properties.getBasePath().resolve(p)).stream())
                .map(r -> properties.isConvertCronJobToJob() && r instanceof CronJob cj ? convertToJob(cj) : r)
                .peek(r -> r.getMetadata().setNamespace(properties.getNamespace()))
                .toList();

        Assert.notEmpty(resources, "Missing resources for " + name + " deployment!");

        Map<String, String> env = mergeEnv(descriptor, deployment);
        resources.stream().map(KubernetesUtils::getPodSpec).filter(Objects::nonNull)
                .flatMap(podSpec -> podSpec.getContainers().stream())
                .forEach(container -> addEnv(container, env));

        Map<Integer, Integer> ports = parsePorts(descriptor);
        resources.stream().map(KubernetesUtils::getServiceSpec).filter(Objects::nonNull)
                .forEach(serviceSpec -> replacePorts(serviceSpec, ports));

        return new KubernetesService(client, resources, deployment);
    }

    @Override
    public boolean supports(DeploymentDescriptor descriptor) {
        return !descriptor.getK8s().isEmpty();
    }

}
