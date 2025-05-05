package com.github.markostijak.jarvis.deployment.docker.listeners;

import static com.github.markostijak.jarvis.deployment.core.internal.listeners.InitializationListener.SERVICE_FACTORY;
import static com.github.markostijak.jarvis.engine.internal.JarvisLifecycleListenerAdapter.ENVIRONMENT;

import com.github.markostijak.jarvis.deployment.core.api.ServiceFactory;
import com.github.markostijak.jarvis.deployment.core.internal.utils.BinderUtils;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentProperties.Deployment;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentRepository;
import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentDescriptor;
import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentProperties;
import com.github.markostijak.jarvis.deployment.docker.DockerServiceFactory;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;

import java.util.Collections;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.testcontainers.DockerClientFactory;

public class DockerInitializationListener implements JarvisLifecycleListener {

    public static final String DOCKER_CLIENT = "docker.client";
    public static final String DOCKER_DEPLOYMENT_PROPERTIES = "docker.deployment.properties";

    @Override
    public void beforeAll(JarvisContext context) {
        Environment environment = context.requireAttribute(ENVIRONMENT);
        DockerDeploymentProperties deploymentProperties = BinderUtils.bind(environment, DockerDeploymentProperties.class);

        Map<String, Deployment<DockerDeploymentDescriptor>> services = deploymentProperties.getServices();
        DeploymentRepository<DockerDeploymentDescriptor> deploymentRepository = new DeploymentRepository<>(
                Collections.unmodifiableMap(services)
        );

        ServiceFactory serviceFactory = new DockerServiceFactory(deploymentRepository);

        context.setAttribute(SERVICE_FACTORY, serviceFactory);
        context.setAttribute(DOCKER_CLIENT, DockerClientFactory.lazyClient());
        context.setAttribute(DOCKER_DEPLOYMENT_PROPERTIES, deploymentProperties);
    }

    @Override
    public void afterAll(JarvisContext context) {
        context.removeAttribute(DOCKER_CLIENT);
    }

    @Override
    public int getOrder() {
        return 101;
    }

}
