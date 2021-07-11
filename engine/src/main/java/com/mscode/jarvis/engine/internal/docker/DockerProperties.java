package com.mscode.jarvis.engine.internal.docker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static com.mscode.jarvis.engine.internal.JarvisProperties.JARVIS_RUNNER;

@Data
@Component
@ConfigurationProperties(prefix = JARVIS_RUNNER + ".docker-compose")
public class DockerProperties {

    /**
     *
     */
    private String context;

    /**
     *
     */
    private Path basePath = Path.of("/");

}
