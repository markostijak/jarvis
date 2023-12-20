package com.github.markostijak.jarvis.deployment.core.support;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DeploymentDescriptor {

    private List<String> ports = emptyList();

    private Map<String, String> env = emptyMap();

    private List<String> volumes = emptyList();

    private Map<String, String> properties = emptyMap();

}
