package com.github.markostijak.jarvis.deployment.core.internal.utils;

import lombok.experimental.UtilityClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

@UtilityClass
public class BinderUtils {

    public static <T> T bind(Environment environment, Class<T> cClass) {
        ConfigurationProperties annotation = AnnotationUtils.findAnnotation(cClass, ConfigurationProperties.class);
        Assert.notNull(annotation, "Missing @ConfigurationProperties annotation on " + cClass.getName());
        return Binder.get(environment).bindOrCreate(annotation.prefix(), cClass);
    }

}
