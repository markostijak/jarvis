package com.github.markostijak.jarvis.deployment.core.api;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

public interface ServiceFactory {

    Service create(JarvisTestContext testContext, Deployment deployment);

}
