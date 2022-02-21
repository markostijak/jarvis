package com.mscode.jarvis.engine.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@Configuration
@EnableAutoConfiguration
public class JarvisTestConfiguration {

    @Bean
    public DependencyInjectionTestExecutionListener dependencyInjectionTestExecutionListener() {
        return new DependencyInjectionTestExecutionListener();
    }

    @Bean
    public JarvisDirtiesContextTestExecutionListener jarvisDirtiesContextTestExecutionListener() {
        return new JarvisDirtiesContextTestExecutionListener();
    }

}
