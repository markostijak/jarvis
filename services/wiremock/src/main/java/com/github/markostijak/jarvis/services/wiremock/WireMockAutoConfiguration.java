package com.github.markostijak.jarvis.services.wiremock;

import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentProperties;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@ComponentScan
@AutoConfiguration
public class WireMockAutoConfiguration {

    @Bean
    @ConditionalOnClass(DockerDeploymentProperties.class)
    public WireMockConnectionDetails dockerWireMockConnectionDetails(Environment environment) {
        String key = "jarvis.deployment.docker.services.wiremock.connection-details";
        return Binder.get(environment).bindOrCreate(key, WireMockConnectionDetails.class);
    }

    @Bean
    @ConditionalOnClass(KubernetesDeploymentProperties.class)
    public WireMockConnectionDetails kubernetesWireMockConnectionDetails(Environment environment) {
        String key = "jarvis.deployment.kubernetes.services.wiremock.connection-details";
        return Binder.get(environment).bindOrCreate(key, WireMockConnectionDetails.class);
    }

    @Bean
    public WireMockClient client(WireMockConnectionDetails connectionDetails) {
        return new WireMockClient(connectionDetails);
    }

}
