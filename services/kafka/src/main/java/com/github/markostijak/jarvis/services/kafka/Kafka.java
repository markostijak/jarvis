package com.github.markostijak.jarvis.services.kafka;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;

@Slf4j
@SuppressWarnings("all")
public class Kafka {

    private static Integer counter = 1;

    private final ProducerFactory<?, ?> producerFactory;
    private final ConsumerFactory<?, ?> consumerFactory;

    private final Map<String, Consumer<?, ?>> consumers = new HashMap<>();

    private KafkaTemplate kafkaTemplate;

    public Kafka(ProducerFactory<?, ?> producerFactory, ConsumerFactory<?, ?> consumerFactory) {
        this.producerFactory = producerFactory;
        this.consumerFactory = consumerFactory;
    }

    public <K, V> CompletableFuture<SendResult<K, V>> publish(String topic, K key, V value) {
        if (kafkaTemplate == null) {
            kafkaTemplate = new KafkaTemplate<>(producerFactory);
        }

        return kafkaTemplate.send(topic, key, value);
    }

    public void subscribeTo(String... topics) {
        subscribeTo(List.of(topics));
    }

    public void subscribeTo(List<String> topics) {
        for (String topic : topics) {
            consumers.computeIfAbsent(topic, this::createConsumerAndSubscribe);
        }
    }

    public <E> E getEvent(String topic) {
        return awaitEvent(topic, Duration.ZERO);
    }

    public <E> E awaitEvent(String topic, String duration) {
        return awaitEvent(topic, DurationStyle.SIMPLE.parse(duration));
    }

    public <E> E awaitEvent(String topic, long amount, TimeUnit timeUnit) {
        return awaitEvent(topic, Duration.ofMillis(timeUnit.toMillis(amount)));
    }

    @SuppressWarnings("unchecked")
    public <E> E awaitEvent(String topic, Duration timeout) {
        Consumer<?, ?> consumer = consumers.computeIfAbsent(topic, this::createConsumerAndSubscribe);
        long end = System.currentTimeMillis() + timeout.abs().toMillis();

        boolean atLeastOnce = true;
        Duration await = Duration.ofMillis(50);

        Object event = null;
        ConsumerRecords<?, ?> records;
        while (atLeastOnce || System.currentTimeMillis() < end) {
            log.debug("Trying to fetch {} events...", topic);
            while ((records = consumer.poll(await)).count() > 0) {
                for (ConsumerRecord<?, ?> record : records) {
                    event = record.value();
                }

                consumer.commitSync();
            }

            if (event != null) {
                return (E) event;
            }

            atLeastOnce = false;
        }

        return (E) event;
    }

    public void unsubscribe() {
        consumers.forEach((topic, consumer) -> {
            consumer.unsubscribe();
            consumer.close();
        });
        consumers.clear();
    }

    private Consumer<?, ?> createConsumerAndSubscribe(String topic) {
        var groupId = consumerFactory.getConfigurationProperties()
                .getOrDefault(ConsumerConfig.GROUP_ID_CONFIG, getClass().getName());

        log.debug("Creating Kafka consumer for topic: {}", topic);
        Consumer<?, ?> consumer = consumerFactory.createConsumer(groupId + "-" + (counter++), counter.toString());
        consumer.subscribe(List.of(topic));

        return consumer;
    }

}
