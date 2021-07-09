package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "jarvis")
public class JarvisProperties {

    /**
     *
     */
    private Runner runner = new Runner();

    /**
     *
     */
    public Map<String, Deployment> services = new HashMap<>();

    @Data
    public static class Runner {

        /**
         *
         */
        private Path logsDirectory = Path.of(System.getProperty("user.dir")).resolve("target").resolve("jarvis");
        /**
         *
         */
        public Kubernetes kubernetes = new Kubernetes();

        /**
         *
         */
        public DockerCompose dockerCompose = new DockerCompose();
    }

    @Data
    public static class Kubernetes {

        /**
         *
         */
        private String context;

        /**
         *
         */
        private Path basePath = Path.of("/");

        private String namespace = "default";
    }

    @Data
    public static class DockerCompose {

        /**
         *
         */
        private String context;

        /**
         *
         */
        private Path basePath = Path.of("/");
    }

    @Data
    public static class Deployment {

        /**
         *
         */
        private DeploymentDescriptor deployment;
    }

}
