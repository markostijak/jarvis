package com.mscode.jarvis.example;

import com.mscode.jarvis.deployment.kafka.DeployKafka;
import com.mscode.jarvis.deployment.kafka.repository.EnableKafkaRepository;
import com.mscode.jarvis.deployment.kafka.repository.KafkaRepository;
import com.mscode.jarvis.engine.annotation.JarvisTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JarvisTest
@DeployKafka(order = 1, delayed = 20)
@EnableKafkaRepository(topics = "test")
public class ExampleTest {

    @Autowired
    private KafkaRepository kafkaRepository;

    @Test
    public void test() {
        String test = kafkaRepository.getEvent("test");
        System.out.println(test);
    }

}
