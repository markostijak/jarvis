package com.mscode.jarvis.runner.internal;

import com.mscode.jarvis.runner.DeploymentDescriptor;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Configuration
@EnableConfigurationProperties(JarvisProperties.class)
public class JarvisConfiguration {

    private final JarvisProperties properties;

    @Autowired
    public JarvisConfiguration(JarvisProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Map<String, DeploymentDescriptor> deploymentDescriptors() {
        return properties.getServices().entrySet().stream().collect(
                toMap(Map.Entry::getKey, e -> e.getValue().getDeployment())
        );
    }

    @Bean
    public KubernetesClient k8sClient() {
        return new DefaultKubernetesClient();
    }

    @Bean
    public ServiceFactory serviceFactory() {
        return new ServiceFactory(properties.getRunner().getKubernetes().getBasePath(), k8sClient());
    }

    @Bean
    public JarvisServiceScheduler jarvisServiceScheduler() {
        return new JarvisServiceScheduler(k8sClient(), serviceFactory(), deploymentDescriptors());
    }

}
