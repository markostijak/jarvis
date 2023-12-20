package com.github.markostijak.jarvis.deployment.docker;

import static com.github.markostijak.jarvis.deployment.core.internal.Labels.DEPLOYMENT;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.RUNNER;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.SCOPE;
import static com.github.markostijak.jarvis.deployment.docker.listeners.DockerNetworkListener.NETWORK;
import static com.github.markostijak.jarvis.engine.api.JarvisContext.JARVIS;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.internal.utils.Deployments;
import com.github.markostijak.jarvis.deployment.core.internal.utils.Utils;
import com.github.markostijak.jarvis.deployment.docker.utils.DockerUtils;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import java.util.Map;
import java.util.function.Consumer;

import com.github.dockerjava.api.command.CreateContainerCmd;
import lombok.RequiredArgsConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

@RequiredArgsConstructor
public class DockerDeploymentCustomizer implements Consumer<Object> {

    private final Deployment deployment;
    private final JarvisTestContext testContext;
    private final DockerDeploymentDescriptor descriptor;

    protected void customize(CreateContainerCmd cmd) {
        String containerName = Utils.orElse(
                descriptor.getContainerName(), deployment.name()
        );

        cmd.withName(containerName);
        Utils.consumeIfAvailable(cmd.getHostConfig(), hc -> {
            hc.withBinds(DockerUtils.parseVolumes(descriptor.getVolumes()));
            hc.withPortBindings(DockerUtils.parsePorts(descriptor.getPorts()));
        });
    }

    protected void customize(GenericContainer<?> container) {
        Map<String, String> envs = Deployments.mergeEnvs(descriptor, deployment);
        Network network = testContext.getParent().getAttribute(NETWORK, Network.SHARED);

        container
                .withEnv(envs)
                .withNetwork(network)
                .withLabels(Map.of(
                        RUNNER, JARVIS,
                        DEPLOYMENT, deployment.name(),
                        SCOPE, deployment.scope().name()
                ));
    }

    @Override
    public void accept(Object element) {
        if (element instanceof CreateContainerCmd cmd) {
            customize(cmd);
        }

        if (element instanceof GenericContainer<?> container) {
            customize(container);
        }
    }

}
