package com.mscode.jarvis.engine.internal.utils;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.TestContext;

import java.util.HashMap;
import java.util.Map;

public class PropertyUtils {

    private static final String JARVIS_PROPERTIES = "jarvisProperties";

    public static void addAsProperty(TestContext context, String name, Object value) {
        addAsProperties(context, Map.of(name, value));
    }

    public static void addAsProperties(TestContext context, Map<String, Object> properties) {
        StandardEnvironment environment = (StandardEnvironment) context.getApplicationContext().getEnvironment();
        MapPropertySource propertySource = (MapPropertySource) environment.getPropertySources().get(JARVIS_PROPERTIES);

        if (propertySource == null) {
            propertySource = new MapPropertySource(JARVIS_PROPERTIES, new HashMap<>());
            environment.getPropertySources().addLast(propertySource);
        }

        propertySource.getSource().putAll(properties);
    }

    public static String getProperty(TestContext context, String property) {
        return getProperty(context, property, null);
    }

    public static String getProperty(TestContext context, String property, String defaultValue) {
        return context.getApplicationContext().getEnvironment().getProperty(property, defaultValue);
    }

}

