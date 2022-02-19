package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "jarvis")
public class JarvisProperties {

    public static final String JARVIS = "jarvis";
    public static final String JARVIS_RUNNER = JARVIS + ".runner";
    public static final String JARVIS_SERVICES = JARVIS + ".services";

    /**
     *
     */
    private RunnerProperties runner = new RunnerProperties();

    /**
     *
     */
    public Map<String, Deployment> services = new HashMap<>();

    @Data
    public static class RunnerProperties {

        /**
         *
         */
        private Duration waitTimeout = Duration.ofMinutes(1);

        /**
         *
         */
        private boolean parallelExecution = true;

        /**
         *
         */
        private Path logsDirectory = Path.of(System.getProperty("user.dir")).resolve("target").resolve("jarvis");

    }

    @Data
    public static class Deployment {

        /**
         *
         */
        private DeploymentDescriptor deployment;
    }

}
