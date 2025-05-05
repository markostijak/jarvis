package com.github.markostijak.jarvis.services.kafka;

import com.github.markostijak.jarvis.deployment.core.internal.utils.BinderUtils;
import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentProperties;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

@ComponentScan
@AutoConfiguration
public class KafkaAutoConfiguration {

    @Bean
    @ConditionalOnClass(DockerDeploymentProperties.class)
    public KafkaPropertiesConnectionDetails dockerKafkaConnectionDetails(Environment environment) {
        return new KafkaPropertiesConnectionDetails(BinderUtils.bind(environment, "docker", "kafka", KafkaProperties.class));
    }

    @Bean
    @ConditionalOnClass(KubernetesDeploymentProperties.class)
    public KafkaPropertiesConnectionDetails kubernetesKafkaConnectionDetails(Environment environment) {
        return new KafkaPropertiesConnectionDetails(BinderUtils.bind(environment, "kubernetes", "kafka", KafkaProperties.class));
    }

    @Bean
    public Kafka kafkaRepository(KafkaPropertiesConnectionDetails connectionDetails) {
        return new Kafka(
                new DefaultKafkaProducerFactory<>(connectionDetails.properties().buildProducerProperties()),
                new DefaultKafkaConsumerFactory<>(connectionDetails.properties().buildConsumerProperties())
        );
    }

}
