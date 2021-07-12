package com.mscode.jarvis.engine.internal.kubernetes;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static com.mscode.jarvis.engine.internal.JarvisProperties.JARVIS_RUNNER;

@Data
@Component
@ConfigurationProperties(prefix = JARVIS_RUNNER + ".kubernetes")
public class KubernetesProperties {

    /**
     *
     */
    private String context;

    /**
     *
     */
    private Path basePath = Path.of("/");

    /**
     *
     */
    private String namespace = "default";

    /**
     *
     */
    private boolean convertCronJobToJob = true;
}
