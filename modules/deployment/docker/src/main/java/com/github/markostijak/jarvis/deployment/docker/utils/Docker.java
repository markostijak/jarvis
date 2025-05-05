package com.github.markostijak.jarvis.deployment.docker.utils;

import java.util.List;
import java.util.Map;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.model.Network;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

@UtilityClass
public class Docker {

    public static CreateNetworkResponse createNetwork(DockerClient client, String name, Map<String, String> labels) {
        return client.createNetworkCmd()
                .withName(name)
                .withLabels(labels)
                .exec();
    }

    public static List<Network> listNetworks(DockerClient client, String name) {
        return client.listNetworksCmd()
                .withNameFilter(name)
                .exec();
    }

    public static Network getNetwork(DockerClient client, String name) {
        List<Network> networks = listNetworks(client, name);

        if (CollectionUtils.isEmpty(networks)) {
            return null;
        }

        return networks.stream().filter(n -> name.equals(n.getName()))
                .findFirst().orElse(null);
    }

    public static void removeNetwork(DockerClient client, String networkId) {
        client.removeNetworkCmd(networkId).exec();
    }

}
