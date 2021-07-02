package com.mscode.jarvis.runner;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jarvis.runner")
public class JarvisProperties {

    public Kubernetes kubernetes = new Kubernetes();

    public DockerCompose dockerCompose = new DockerCompose();

    @Data

    public static class Kubernetes {

        private String context;

    }

    @Data
    public static class DockerCompose {

        private String context;

    }

}
