package com.mscode.jarvis.deployment.kafka.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaRepositoryConfiguration {

    @Bean
    public KafkaRepository kafkaRepository() {
        return new KafkaRepository();
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public KafkaExecutionListener kafkaExecutionListener(ConsumerFactory<?, ?> consumerFactory) {
        return new KafkaExecutionListener(consumerFactory, kafkaRepository());
    }

}
