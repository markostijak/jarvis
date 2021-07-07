package com.mscode.jarvis.engine.api;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import org.springframework.core.annotation.MergedAnnotation;

public interface ServiceFactory {

    Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment);

}
