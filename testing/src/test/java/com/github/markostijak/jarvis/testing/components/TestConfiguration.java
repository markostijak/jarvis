package com.github.markostijak.jarvis.testing.components;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Bean
    public TestBean test() {
        return new TestBean("test");
    }

    public record TestBean(String name) {
    }

}
