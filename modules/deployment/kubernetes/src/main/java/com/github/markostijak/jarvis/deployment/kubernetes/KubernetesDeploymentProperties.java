package com.github.markostijak.jarvis.deployment.kubernetes;

import static com.github.markostijak.jarvis.engine.api.JarvisContext.JARVIS;
import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

import com.github.markostijak.jarvis.deployment.core.support.DeploymentProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("jarvis.deployment.kubernetes")
public class KubernetesDeploymentProperties extends DeploymentProperties<KubernetesDeploymentDescriptor> {

    private String namespace = JARVIS;

    private String basePath = CLASSPATH_URL_PREFIX;

    private boolean convertCronJobToJob = true;

}
