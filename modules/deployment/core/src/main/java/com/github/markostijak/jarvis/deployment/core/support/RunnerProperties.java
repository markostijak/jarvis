package com.github.markostijak.jarvis.deployment.core.support;

import java.nio.file.Path;
import java.time.Duration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("jarvis.runner")
public class RunnerProperties {

    private Duration waitTimeout = Duration.ofMinutes(1);

    private Path logsDirectory = Path.of(System.getProperty("user.dir")).resolve("logs").resolve("jarvis");

}
