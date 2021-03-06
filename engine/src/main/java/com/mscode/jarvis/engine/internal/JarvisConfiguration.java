package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.api.ServiceFactory;
import com.mscode.jarvis.engine.api.ServiceScheduler;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Configuration
@ComponentScan
@ImportComponents
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
    public KubernetesClient k8sClient() {
        return new DefaultKubernetesClient();
    }

    @Bean
    @Autowired
    public JarvisServiceFactory jarvisServiceFactory(List<ServiceFactory> factories) {
        return new JarvisServiceFactory(descriptorRepository(), factories);
    }

    @Bean
    @Autowired
    public JarvisServiceScheduler jarvisServiceScheduler(JarvisServiceFactory factory, ServiceScheduler serviceScheduler) {
        return new JarvisServiceScheduler(properties.getRunner(), factory, serviceScheduler);
    }

}
