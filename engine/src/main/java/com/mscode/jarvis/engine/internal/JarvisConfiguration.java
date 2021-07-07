package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.api.ServiceFactory;
import com.mscode.jarvis.engine.internal.docker.DockerServiceFactory;
import com.mscode.jarvis.engine.internal.kubernetes.KubernetesServiceFactory;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
    public JarvisDescriptorRepository descriptorRepository() {
        return new JarvisDescriptorRepository(
                properties.getServices().entrySet().stream().collect(
                        toMap(Map.Entry::getKey, e -> e.getValue().getDeployment())
                )
        );
    }

    @Bean
    @Profile("!docker")
    public KubernetesClient k8sClient() {
        return new DefaultKubernetesClient();
    }

    @Bean
    @Profile("!docker")
    public KubernetesServiceFactory kubernetesServiceFactory() {
        return new KubernetesServiceFactory(properties.getRunner().getKubernetes().getBasePath(), k8sClient());
    }

    @Bean
    @Profile("docker")
    public DockerServiceFactory dockerServiceFactory() {
        return new DockerServiceFactory();
    }

    @Bean
    public JarvisServiceScheduler jarvisServiceScheduler(ServiceFactory serviceFactory) {
        return new JarvisServiceScheduler(serviceFactory, descriptorRepository());
    }

}
