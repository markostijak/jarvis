package com.mscode.jarvis.deployment.kafka.repository;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

@Component
public class KafkaRepository {

    private Map<String, Consumer<?, ?>> consumers = emptyMap();

    @SuppressWarnings("unchecked")
    public synchronized <E> E awaitEvent(String topic, int amount, TimeUnit timeUnit) {
        Consumer<?, ?> consumer = requireNonNull(consumers.get(topic));
        Duration await = Duration.ofMillis(timeUnit.toMillis(amount));

        Object event = null;

        ConsumerRecords<?, ?> records;
        while ((records = consumer.poll(await)).count() > 0) {
            for (ConsumerRecord<?, ?> record : records) {
                event = record.value();
            }

            consumer.commitSync();
        }

        return (E) event;
    }

    public <E> E getEvent(String topic) {
        return awaitEvent(topic, 0, TimeUnit.MILLISECONDS);
    }

    public <E> E awaitEvent(String topic) {
        return awaitEvent(topic, 5, TimeUnit.SECONDS);
    }

    void registerConsumers(Map<String, Consumer<?, ?>> consumers) {
        this.consumers = consumers;
    }

    void reset() {
        this.consumers = emptyMap();
    }

}
