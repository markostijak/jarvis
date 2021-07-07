package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceFactory;
import org.springframework.core.annotation.MergedAnnotation;

import java.util.List;

public class JarvisDelegatingFactory implements ServiceFactory {

    private final List<ServiceFactory> serviceFactories;

    public JarvisDelegatingFactory(List<ServiceFactory> serviceFactories) {
        this.serviceFactories = serviceFactories;
    }

    @Override
    public Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        for (ServiceFactory factory : serviceFactories) {
            if (factory.supports(descriptor)) {
                return factory.create(descriptor, deployment);
            }
        }

        throw new IllegalStateException("Invalid deployment descriptor for " + deployment.getString("name"));
    }

    @Override
    public boolean supports(DeploymentDescriptor descriptor) {
        return true;
    }

}
