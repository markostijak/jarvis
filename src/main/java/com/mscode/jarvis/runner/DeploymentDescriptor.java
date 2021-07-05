package com.mscode.jarvis.runner;

import lombok.Data;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@Data
public class DeploymentDescriptor {

    private static DeploymentDescriptor EMPTY = new DeploymentDescriptor();

    public static DeploymentDescriptor empty() {
        return EMPTY;
    }

    /**
     *
     */
    private List<Path> paths = emptyList();

    /**
     *
     */
    private Map<String, String> env = emptyMap();

    /**
     *
     */
    private Map<String, String> properties = emptyMap();

}
