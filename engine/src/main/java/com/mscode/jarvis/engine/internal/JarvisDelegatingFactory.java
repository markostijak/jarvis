package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceFactory;
import com.mscode.jarvis.engine.internal.docker.DockerServiceFactory;
import com.mscode.jarvis.engine.internal.kubernetes.KubernetesServiceFactory;
import org.springframework.core.annotation.MergedAnnotation;

public class JarvisDelegatingFactory implements ServiceFactory {

    private final DockerServiceFactory dockerServiceFactory;
    private final KubernetesServiceFactory kubernetesServiceFactory;

    public JarvisDelegatingFactory(DockerServiceFactory dockerServiceFactory, KubernetesServiceFactory kubernetesServiceFactory) {
        this.dockerServiceFactory = dockerServiceFactory;
        this.kubernetesServiceFactory = kubernetesServiceFactory;
    }

    @Override
    public Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        if (!descriptor.getK8s().isEmpty()) {
            return kubernetesServiceFactory.create(descriptor, deployment);
        }

        if (descriptor.getImage() != null) {
            return dockerServiceFactory.create(descriptor, deployment);
        }

        throw new IllegalStateException("Invalid deployment descriptor for " + deployment.getString("name"));
    }

}
