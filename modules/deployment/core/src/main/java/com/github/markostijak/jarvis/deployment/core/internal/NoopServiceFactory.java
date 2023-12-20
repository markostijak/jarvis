package com.github.markostijak.jarvis.deployment.core.internal;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Await;
import com.github.markostijak.jarvis.deployment.core.api.Service;
import com.github.markostijak.jarvis.deployment.core.api.ServiceFactory;
import com.github.markostijak.jarvis.deployment.core.support.AbstractService;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

public class NoopServiceFactory implements ServiceFactory {

    @Override
    public Service create(JarvisTestContext testContext, Deployment deployment) {
        return new AbstractService(deployment) {
            @Override
            public Await start() {
                return timeout -> {};
            }

            @Override
            public boolean stop() {
                return true;
            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }

}
