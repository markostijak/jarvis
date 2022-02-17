package com.mscode.jarvis.deployment.kafka.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.emptyMap;
import static org.springframework.test.context.TestContextAnnotationUtils.findMergedAnnotation;

@Slf4j
public class KafkaExecutionListener implements TestExecutionListener {

    private final KafkaRepository kafkaRepository;
    private final ConsumerFactory<?, ?> consumerFactory;

    private Map<String, Consumer<?, ?>> consumers = emptyMap();

    public KafkaExecutionListener(ConsumerFactory<?, ?> consumerFactory, KafkaRepository kafkaRepository) {
        this.consumerFactory = consumerFactory;
        this.kafkaRepository = kafkaRepository;
    }

    @Override
    public void beforeTestClass(@NonNull TestContext testContext) {
        EnableKafkaRepository kafka = findMergedAnnotation(testContext.getTestClass(), EnableKafkaRepository.class);

        if (kafka == null || kafka.topics().length == 0) {
            return;
        }

        consumers = new ConcurrentHashMap<>();
        Set<String> topics = Set.of(kafka.topics());

        for (String topic : topics) {
            Consumer<?, ?> consumer = consumerFactory.createConsumer();
            consumer.subscribe(List.of(topic));
            consumers.put(topic, consumer);
            log.info("Created KafkaConsumer for {} topic", topic);
        }

        kafkaRepository.registerConsumers(consumers);
    }

    @Override
    public void afterTestClass(@NonNull TestContext testContext) {
        kafkaRepository.reset();
        consumers.forEach((topic, consumer) -> {
            consumer.unsubscribe();
            consumer.close();
            log.info("Closed KafkaConsumer for {} topic", topic);
        });
    }

}
