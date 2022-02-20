package com.mscode.jarvis.engine.internal.docker;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;

@Order(3)
@Component
public class DockerServiceFactory implements ServiceFactory {

    private final DockerProperties properties;

    @Autowired
    public DockerServiceFactory(DockerProperties properties) {
        this.properties = properties;
    }

    @Override
    public Service create(TestContext context, DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        return null;
    }

    @Override
    public boolean supports(DeploymentDescriptor descriptor) {
        return descriptor.getImage() != null;
    }

}
