package com.github.markostijak.jarvis.testing;

import com.github.markostijak.jarvis.services.kafka.DeployKafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@DeployKafka
@WebMvcTest(JarvisController.class)
public class SpringWebMvcIntegrationTest {

    @Test
    void test() {
        System.out.println("test");
    }

}
