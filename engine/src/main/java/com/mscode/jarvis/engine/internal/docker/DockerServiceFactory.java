package com.mscode.jarvis.engine.internal.docker;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceFactory;
import org.springframework.core.annotation.MergedAnnotation;

public class DockerServiceFactory implements ServiceFactory {
    @Override
    public Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        return null;
    }
}
