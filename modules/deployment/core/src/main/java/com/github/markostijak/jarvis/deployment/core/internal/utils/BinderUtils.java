package com.github.markostijak.jarvis.deployment.core.internal.utils;

import lombok.experimental.UtilityClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

@UtilityClass
public class BinderUtils {

    public static <T> T bind(Environment environment, Class<T> type) {
        ConfigurationProperties annotation = AnnotationUtils.findAnnotation(type, ConfigurationProperties.class);
        Assert.notNull(annotation, "Missing @ConfigurationProperties annotation on " + type.getName());
        return Binder.get(environment).bindOrCreate(annotation.prefix(), type);
    }

    public static <T> T bind(Environment environment, String runner, String service, Class<T> type) {
        final String prefix = "jarvis.deployment.%s.services.%s.connection-details";
        return Binder.get(environment).bindOrCreate(prefix.formatted(runner, service), type);
    }

}
