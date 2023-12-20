package com.github.markostijak.jarvis.deployment.docker;

import com.github.markostijak.jarvis.deployment.core.support.DeploymentDescriptor;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DockerDeploymentDescriptor extends DeploymentDescriptor {

    private String image;

    private String containerName;

}
