package com.github.markostijak.jarvis.deployment.kubernetes;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Service;
import com.github.markostijak.jarvis.deployment.core.api.ServiceFactory;
import com.github.markostijak.jarvis.deployment.core.internal.utils.Deployments;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentRepository;
import com.github.markostijak.jarvis.deployment.kubernetes.utils.Kubernetes;
import com.github.markostijak.jarvis.deployment.kubernetes.utils.KubernetesResourceUtils;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import java.io.InputStream;
import java.util.List;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KubernetesServiceFactory implements ServiceFactory {

    private final KubernetesClient client;
    private final KubernetesResourceLoader kubernetesResourceLoader;
    private final DeploymentRepository<KubernetesDeploymentDescriptor> repository;
    private final KubernetesDeploymentProperties properties;

    @Override
    public Service create(JarvisTestContext testContext, Deployment deployment) {
        var descriptor = resolveDeploymentDescriptorFor(deployment);
        var logDirectory = Deployments.resolveDirectoryFor(testContext, deployment);
        var customizer = new KubernetesDeploymentCustomizer(deployment, testContext, descriptor);

        InputStream content = kubernetesResourceLoader.load(deployment.name());
        List<HasMetadata> resources = Kubernetes.load(client, content, customizer);

        if (properties.isConvertCronJobToJob()) {
            resources = KubernetesResourceUtils.convertToJob(resources);
        }

        return new KubernetesService(client, resources, deployment, logDirectory);
    }

    protected KubernetesDeploymentDescriptor resolveDeploymentDescriptorFor(Deployment deployment) {
        if (deployment instanceof KubernetesDeploymentBean bean) {
            return bean.descriptor();
        }

        return repository.getByName(deployment.name()).descriptor();
    }

}
