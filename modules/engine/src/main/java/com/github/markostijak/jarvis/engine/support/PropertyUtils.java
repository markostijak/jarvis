package com.github.markostijak.jarvis.engine.support;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

@UtilityClass
public class PropertyUtils {

    public static final String JARVIS_PROPERTIES = "jarvisProperties";

    public static void addAsProperties(Environment environment, Map<String, Object> properties) {
        if (environment instanceof ConfigurableEnvironment ce) {
            addAsProperties(ce, properties);
            return;
        }

        throw new UnsupportedOperationException("Environment '" + environment.getClass() + "' is not configurable");
    }

    public static void addAsProperty(Environment environment, String name, Object value) {
        addAsProperties(environment, Map.of(name, value));
    }

    public static void addAsProperties(ConfigurableEnvironment environment, Map<String, Object> properties) {
        MapPropertySource propertySource = (MapPropertySource) environment.getPropertySources().get(JARVIS_PROPERTIES);

        if (propertySource == null) {
            propertySource = new MapPropertySource(JARVIS_PROPERTIES, new HashMap<>());
            environment.getPropertySources().addLast(propertySource);
        }

        propertySource.getSource().putAll(properties);
    }

}
