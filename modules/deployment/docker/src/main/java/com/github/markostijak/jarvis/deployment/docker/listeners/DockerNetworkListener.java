package com.github.markostijak.jarvis.deployment.docker.listeners;

import static com.github.markostijak.jarvis.deployment.core.internal.Labels.NAME;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.RUNNER;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.SCOPE;
import static com.github.markostijak.jarvis.deployment.docker.listeners.DockerInitializationListener.DOCKER_CLIENT;
import static com.github.markostijak.jarvis.deployment.docker.listeners.DockerInitializationListener.DOCKER_DEPLOYMENT_PROPERTIES;
import static com.github.markostijak.jarvis.engine.api.JarvisContext.JARVIS;

import java.util.List;
import java.util.Map;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Network;
import com.github.markostijak.jarvis.deployment.core.api.Scope;
import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentProperties;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;
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

        List<Network> networks = client.listNetworksCmd()
                .withNameFilter(name)
                .exec();

        if (!networks.isEmpty()) {
            return;
        }

        client.createNetworkCmd()
                .withName(name)
                .withLabels(Map.of(
                        NAME, name,
                        RUNNER, JARVIS,
                        SCOPE, Scope.JVM.name()
                )).exec();

        Network network = client.listNetworksCmd()
                .withNameFilter(name)
                .exec()
                .get(0);

        log.debug("Created a new network: '{}'", network.getName());
        context.setAttribute(NETWORK, network);
    }

    @Override
    public void afterAll(JarvisContext context) {
        Network network = (Network) context.removeAttribute(NETWORK);

        if (network != null) {
            client.removeNetworkCmd(network.getName()).exec();
            log.debug("Deleted {} network", network.getName());
        }
    }

    @Override
    public int getOrder() {
        return 1701;
    }

}
