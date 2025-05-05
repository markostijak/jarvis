package com.github.markostijak.jarvis.deployment.docker.utils;

import java.util.Collections;
import java.util.List;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.PortBinding;
import org.springframework.util.CollectionUtils;

public class DockerUtils {

    public static List<PortBinding> parsePorts(List<String> ports) {
        if (CollectionUtils.isEmpty(ports)) {
            return Collections.emptyList();
        }

        return ports.stream().map(PortBinding::parse).toList();
    }

    public static Integer[] parseExposedPorts(List<String> ports) {
        return parsePorts(ports).stream()
                .map(p -> p.getExposedPort().getPort())
                .toArray(Integer[]::new);
    }

    public static List<Bind> parseVolumes(List<String> volumes) {
        if (CollectionUtils.isEmpty(volumes)) {
            return Collections.emptyList();
        }

        return volumes.stream().map(Bind::parse).toList();
    }

}
