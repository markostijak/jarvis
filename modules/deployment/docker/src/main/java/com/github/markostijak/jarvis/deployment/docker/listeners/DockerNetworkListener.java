package com.github.markostijak.jarvis.deployment.docker.listeners;

import static com.github.markostijak.jarvis.deployment.core.internal.Labels.NAME;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.RUNNER;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.SCOPE;
import static com.github.markostijak.jarvis.deployment.docker.listeners.DockerInitializationListener.DOCKER_CLIENT;
import static com.github.markostijak.jarvis.deployment.docker.listeners.DockerInitializationListener.DOCKER_DEPLOYMENT_PROPERTIES;
import static com.github.markostijak.jarvis.engine.api.JarvisContext.JARVIS;

import com.github.markostijak.jarvis.deployment.core.api.Scope;
import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentProperties;
import com.github.markostijak.jarvis.deployment.docker.utils.Docker;
import com.github.markostijak.jarvis.deployment.docker.utils.DockerNetwork;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;

import java.util.Map;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Network;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DockerNetworkListener implements JarvisLifecycleListener {

    public static final String NETWORK = DockerNetworkListener.class + ".network";

    private DockerClient client;
    private DockerDeploymentProperties properties;

    @Override
    public void beforeAll(JarvisContext context) {
        client = context.requireAttribute(DOCKER_CLIENT);
        properties = context.requireAttribute(DOCKER_DEPLOYMENT_PROPERTIES);

        String name = properties.getNetwork();
        Network network = Docker.getNetwork(client, name);

        if (network == null) {
            String networkId = Docker.createNetwork(client, name, Map.of(
                    NAME, name,
                    RUNNER, JARVIS,
                    SCOPE, Scope.JVM.name()
            )).getId();

            log.debug("Created a new network: '{}'", name);
            context.setAttribute(NETWORK, DockerNetwork.nonExisting(networkId, name));
        } else {
            log.debug("Using an existing network: '{}'", name);
            context.setAttribute(NETWORK, DockerNetwork.existing(network));
        }
    }

    @Override
    public void afterAll(JarvisContext context) {
        DockerNetwork network = (DockerNetwork) context.removeAttribute(NETWORK);

        if (network != null && !network.isExisting()) {
            try {
                Docker.removeNetwork(client, network.getId());
                log.debug("Deleted {} network", network.getName());
            } catch (Exception ex) {
                log.warn("Error while removing {} network", network.getName(), ex);
            }
        }
    }

    @Override
    public int getOrder() {
        return 1701;
    }

}
