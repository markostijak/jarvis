package com.mscode.jarvis.engine;

import com.mscode.jarvis.engine.internal.helm.HelmChart;
import lombok.Data;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@Data
public class DeploymentDescriptor {

    private static final DeploymentDescriptor EMPTY = new DeploymentDescriptor();

    /**
     *
     */
    private List<Path> k8s = emptyList();

    /**
     *
     */
    private HelmChart helm;

    /**
     *
     */
    private String image;

    /**
     *
     */
    private List<String> ports = emptyList();

    /**
     *
     */
    private Map<String, String> env = emptyMap();

    /**
     *
     */
    private List<String> volumes = emptyList();

    /**
     *
     */
    private Map<String, String> properties = emptyMap();

    public static DeploymentDescriptor empty() {
        return EMPTY;
    }

}
