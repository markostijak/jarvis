package com.github.markostijak.jarvis.deployment.docker;

import static com.github.markostijak.jarvis.engine.api.JarvisContext.JARVIS;

import com.github.markostijak.jarvis.deployment.core.support.DeploymentProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("jarvis.deployment.docker")
public class DockerDeploymentProperties extends DeploymentProperties<DockerDeploymentDescriptor> {

    private String network = JARVIS;

}
