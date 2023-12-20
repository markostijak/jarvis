package com.github.markostijak.jarvis.services.kafka;

import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentProperties;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.bind.Binder;
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
        String key = "jarvis.deployment.docker.services.kafka.connection-details";
        KafkaProperties properties = Binder.get(environment).bindOrCreate(key, KafkaProperties.class);
        return new KafkaPropertiesConnectionDetails(properties);
    }

    @Bean
    @ConditionalOnClass(KubernetesDeploymentProperties.class)
    public KafkaPropertiesConnectionDetails kubernetesKafkaConnectionDetails(Environment environment) {
        String key = "jarvis.deployment.kubernetes.services.kafka.connection-details";
        KafkaProperties properties = Binder.get(environment).bindOrCreate(key, KafkaProperties.class);
        return new KafkaPropertiesConnectionDetails(properties);
    }

    @Bean
    public Kafka kafkaRepository(KafkaPropertiesConnectionDetails connectionDetails) {
        return new Kafka(
                new DefaultKafkaProducerFactory<>(connectionDetails.properties().buildProducerProperties()),
                new DefaultKafkaConsumerFactory<>(connectionDetails.properties().buildConsumerProperties())
        );
    }

}
