package com.github.markostijak.jarvis.services.kafka;

import java.util.List;

import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

public record KafkaPropertiesConnectionDetails(KafkaProperties properties) implements KafkaConnectionDetails {

    @Override
    public List<String> getBootstrapServers() {
        return this.properties.getBootstrapServers();
    }

    @Override
    public List<String> getConsumerBootstrapServers() {
        return getServers(this.properties.getConsumer().getBootstrapServers());
    }

    @Override
    public List<String> getProducerBootstrapServers() {
        return getServers(this.properties.getProducer().getBootstrapServers());
    }

    @Override
    public List<String> getStreamsBootstrapServers() {
        return getServers(this.properties.getStreams().getBootstrapServers());
    }

    private List<String> getServers(List<String> servers) {
        return (servers != null) ? servers : getBootstrapServers();
    }

}
