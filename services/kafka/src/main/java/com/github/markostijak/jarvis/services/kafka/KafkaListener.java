package com.github.markostijak.jarvis.services.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

@Component
@RequiredArgsConstructor
public class KafkaListener implements TestExecutionListener, Ordered {

    private final Kafka kafka;

    @Override
    public void afterTestMethod(@NonNull TestContext testContext) {
        kafka.unsubscribe();
    }

    @Override
    public int getOrder() {
        return 102;
    }

}
