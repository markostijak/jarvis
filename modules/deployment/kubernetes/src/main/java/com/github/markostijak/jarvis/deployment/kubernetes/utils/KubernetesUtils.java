package com.github.markostijak.jarvis.deployment.kubernetes.utils;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KubernetesUtils {

    public Map<Integer, Integer> parsePorts(List<String> ports) {
        return ports.stream().map(p -> p.split(":", -1))
                .collect(toMap(e -> Integer.parseInt(e[1]), e -> Integer.parseInt(e[0])));
    }

    public Map<String, String> parseVolumes(List<String> volumes) {
        return volumes.stream().map(p -> p.split(":", -1))
                .collect(toMap(e -> e[1], e -> e[0]));
    }

}
