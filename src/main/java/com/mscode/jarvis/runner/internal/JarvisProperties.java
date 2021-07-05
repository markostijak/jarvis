package com.mscode.jarvis.runner.internal;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@Data
@ConfigurationProperties(prefix = "jarvis.runner")
public class JarvisProperties {

    public Kubernetes kubernetes = new Kubernetes();

    public DockerCompose dockerCompose = new DockerCompose();

    @Data

    public static class Kubernetes {

        private String context;

        private Path basePath = Path.of("/");

    }

    @Data
    public static class DockerCompose {

        private String context;

        private Path basePath = Path.of("/");

    }

}
