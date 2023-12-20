package com.github.markostijak.jarvis.engine.internal;

import com.github.markostijak.jarvis.engine.JarvisTest;

import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContextAnnotationUtils;

public class JarvisTestContextBootstrapper extends SpringBootTestContextBootstrapper {

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        System.setProperty("spring.config.name", "jarvis, properties, application");
    }

    @Override
    protected String[] getProperties(Class<?> testClass) {
        JarvisTest jarvisTest = TestContextAnnotationUtils.findMergedAnnotation(testClass, JarvisTest.class);
        return (jarvisTest != null) ? jarvisTest.properties() : null;
    }

    @Override
    protected Class<?>[] getOrFindConfigurationClasses(MergedContextConfiguration mergedConfig) {
        return new Class<?>[]{JarvisTestConfiguration.class};
    }

}
