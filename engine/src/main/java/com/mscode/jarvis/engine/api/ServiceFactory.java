package com.mscode.jarvis.engine.api;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.test.context.TestContext;

public interface ServiceFactory {

    Service create(TestContext context, DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment);

    boolean supports(DeploymentDescriptor descriptor);

}
