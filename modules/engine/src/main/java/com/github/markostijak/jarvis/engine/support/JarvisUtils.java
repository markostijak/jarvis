package com.github.markostijak.jarvis.engine.support;

import com.github.markostijak.jarvis.engine.JarvisTest;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;

@UtilityClass
public class JarvisUtils {

    public static JarvisTest getAnnotation(Class<?> testClass) {
        return AnnotationUtils.getAnnotation(testClass, JarvisTest.class);
    }

    public static JarvisContext getJarvisContext(TestContext testContext) {
        return getJarvisTestContext(testContext).getParent();
    }

    public static JarvisTestContext getJarvisTestContext(TestContext testContext) {
        return (JarvisTestContext) testContext.getAttribute(JarvisTestContext.class.getName());
    }

    public static Map<String, String> parseProperties(JarvisTest jarvisTest) {
        if (jarvisTest == null || jarvisTest.properties() == null) {
            return Collections.emptyMap();
        }

        Map<String, String> properties = new HashMap<>();
        for (String property : jarvisTest.properties()) {
            String[] parts = property.split("=");
            properties.put(parts[0], parts[1]);
        }

        return properties;
    }

}
