package com.github.markostijak.jarvis.deployment.kubernetes;

import com.github.markostijak.jarvis.deployment.core.support.DeploymentDescriptor;
import com.github.markostijak.jarvis.deployment.kubernetes.helm.HelmChart;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KubernetesDeploymentDescriptor extends DeploymentDescriptor {

    private String yaml;

    private HelmChart helm;

}
