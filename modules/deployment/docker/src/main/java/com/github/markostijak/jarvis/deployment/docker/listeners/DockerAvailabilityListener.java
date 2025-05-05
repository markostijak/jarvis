package com.github.markostijak.jarvis.deployment.docker.listeners;

import static com.github.markostijak.jarvis.deployment.docker.listeners.DockerInitializationListener.DOCKER_CLIENT;
import static com.github.markostijak.jarvis.engine.internal.JarvisLifecycleListenerAdapter.ENVIRONMENT;

import com.github.markostijak.jarvis.deployment.core.internal.exceptions.JarvisDeploymentException;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentExecutionStrategy;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;
import com.github.markostijak.jarvis.engine.internal.exceptions.JarvisExecutionException;

import com.github.dockerjava.api.DockerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

@Slf4j
public class DockerAvailabilityListener implements JarvisLifecycleListener {

    @Override
    @SuppressWarnings("resource")
    public void beforeAll(JarvisContext context) {
        Environment environment = context.requireAttribute(ENVIRONMENT);

        if (DeploymentExecutionStrategy.isJUnitParallelMode(environment)) {
            log.error("Jarvis Docker runner does not support parallel execution mode");
            throw new JarvisExecutionException("Unsupported execution mode");
        }

        DockerClient client = context.requireAttribute(DOCKER_CLIENT);

        try {
            client.pingCmd().exec();
        } catch (Exception e) {
            log.error("Docker is not running. Aborting...");
            throw new JarvisDeploymentException("Please check if Docker is up and running!", e);
        }
    }

    @Override
    public int getOrder() {
        return 1701;
    }

}
