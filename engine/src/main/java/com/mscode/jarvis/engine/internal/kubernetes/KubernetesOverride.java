package com.mscode.jarvis.engine.internal.kubernetes;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class KubernetesOverride {

    private String name;

    private String namespace;

    private Map<Integer, Integer> ports;

    private Map<String, String> env;

    private Map<String, String> volumes;

}
