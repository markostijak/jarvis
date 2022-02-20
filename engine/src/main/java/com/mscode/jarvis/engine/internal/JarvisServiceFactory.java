package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.test.context.TestContext;

import java.util.List;

@Slf4j
public class JarvisServiceFactory {

    private final List<ServiceFactory> factories;
    private final JarvisDescriptorRepository repository;

    public JarvisServiceFactory(JarvisDescriptorRepository repository, List<ServiceFactory> factories) {
        this.repository = repository;
        this.factories = factories;
    }

    public Service create(TestContext context, MergedAnnotation<Deployment> deployment) {
        String name = deployment.getString("name");
        DeploymentDescriptor descriptor = repository.getByName(name);

        for (ServiceFactory factory : factories) {
            if (factory.supports(descriptor)) {
                return factory.create(context, descriptor, deployment);
            }
        }

        throw new IllegalStateException("Invalid deployment descriptor for " + name);
    }

}
