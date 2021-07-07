package com.mscode.jarvis.deployment.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ComponentScan
@Configuration(proxyBeanMethods = false)
public class KafkaConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "jarvis.services.kafka.connection")
    public KafkaProperties kafkaProperties() {
        return new KafkaProperties();
    }

}
