package com.github.markostijak.jarvis.deployment.docker;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Service;
import com.github.markostijak.jarvis.deployment.core.api.ServiceFactory;
import com.github.markostijak.jarvis.deployment.core.internal.utils.Deployments;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentRepository;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import lombok.RequiredArgsConstructor;
import org.testcontainers.containers.GenericContainer;

@RequiredArgsConstructor
public class DockerServiceFactory implements ServiceFactory {

    private final DeploymentRepository<DockerDeploymentDescriptor> repository;

    @Override
    @SuppressWarnings("resource")
    public Service create(JarvisTestContext context, Deployment deployment) {
        var descriptor = resolveDeploymentDescriptorFor(deployment);
        var logDirectory = Deployments.resolveDirectoryFor(context, deployment);
        var customizer = new DockerDeploymentCustomizer(deployment, context, descriptor);

        GenericContainer<?> container = new GenericContainer<>(descriptor.getImage())
                .withCreateContainerCmdModifier(customizer::accept);

        customizer.accept(container);

        return new DockerService(container, deployment, logDirectory);
    }

    protected DockerDeploymentDescriptor resolveDeploymentDescriptorFor(Deployment deployment) {
        if (deployment instanceof DockerDeploymentBean bean) {
            return bean.descriptor();
        }

        return repository.getByName(deployment.name()).descriptor();
    }

}
