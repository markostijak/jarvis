package com.mscode.jarvis.engine.internal;

import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;

import java.util.Set;

import static java.util.Collections.emptySet;
import static org.springframework.util.ObjectUtils.containsElement;

public class JarvisTestContextBootstrapper extends DefaultTestContextBootstrapper {

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
    }

    @NonNull
    @Override
    protected MergedContextConfiguration processMergedContextConfiguration(@NonNull MergedContextConfiguration config) {
        if (containsElement(config.getClasses(), JarvisConfiguration.class)) {
            return buildMergedContextConfiguration(config, emptySet()); // ignore context customizers
        }

        return super.processMergedContextConfiguration(config);
    }

    private MergedContextConfiguration buildMergedContextConfiguration(MergedContextConfiguration config, Set<ContextCustomizer> customizers) {
        return new MergedContextConfiguration(config.getTestClass(), config.getLocations(),
                config.getClasses(), config.getContextInitializerClasses(), config.getActiveProfiles(),
                config.getPropertySourceLocations(), config.getPropertySourceProperties(), customizers,
                config.getContextLoader(), getCacheAwareContextLoaderDelegate(), config.getParent()
        );
    }

    @NonNull
    @Override
    protected Class<? extends ContextLoader> getDefaultContextLoaderClass(@NonNull Class<?> testClass) {
        throw new UnsupportedOperationException("Use @ContextConfiguration annotation to define ContextLoader!");
    }

    public static class JarvisContextLoader extends AnnotationConfigContextLoader {
        @Override
        protected void prepareContext(@NonNull GenericApplicationContext applicationContext) {
            new ConfigDataApplicationContextInitializer().initialize(applicationContext);
        }
    }

    public static class JarvisTestContextLoader extends AnnotationConfigContextLoader {
        @Override
        protected void prepareContext(@NonNull GenericApplicationContext context) {
            context.addBeanFactoryPostProcessor(beanFactory -> {
                for (String beanName : beanFactory.getBeanDefinitionNames()) {
                    beanFactory.getBeanDefinition(beanName).setLazyInit(true);
                }
            });
        }
    }

}
