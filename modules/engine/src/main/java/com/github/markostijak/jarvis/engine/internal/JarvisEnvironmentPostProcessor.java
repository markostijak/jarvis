package com.github.markostijak.jarvis.engine.internal;

import com.github.markostijak.jarvis.engine.JarvisTest;
import com.github.markostijak.jarvis.engine.support.PropertyUtils;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class JarvisEnvironmentPostProcessor implements EnvironmentPostProcessor {
    /**
     * Disables autoconfiguration for tests annotated with {@link JarvisTest}.
     * This still can be overridden using application, system or environment properties.
     *
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (application.getMainApplicationClass().isAnnotationPresent(JarvisTest.class)) {
            PropertyUtils.addAsProperty(environment, "spring.boot.enableautoconfiguration", "false");
        }
    }
}
