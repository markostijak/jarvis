package com.github.markostijak.jarvis.deployment.docker.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.rules.ExternalResource;
import org.testcontainers.containers.Network;

@Getter
@RequiredArgsConstructor
public class DockerNetwork extends ExternalResource implements Network {

    private final String id;

    private final String name;

    private final boolean existing;

    @Override
    public void close() {
        // no-op
    }

    public static Network nonExisting(String networkId, String name) {
        return new DockerNetwork(networkId, name, false);
    }

    public static Network existing(com.github.dockerjava.api.model.Network network) {
        return new DockerNetwork(network.getId(), network.getName(), true);
    }

}
