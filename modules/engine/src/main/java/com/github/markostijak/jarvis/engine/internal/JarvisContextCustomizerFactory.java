package com.github.markostijak.jarvis.engine.internal;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

@Order(100)
public class JarvisContextCustomizerFactory implements ContextCustomizerFactory {
    /**
     * Create a {@link ContextCustomizer} that should be used to customize a
     * {@link org.springframework.context.ConfigurableApplicationContext ConfigurableApplicationContext}
     * before it is refreshed.
     *
     * @param testClass        the test class
     * @param configAttributes the list of context configuration attributes for
     *                         the test class, ordered <em>bottom-up</em> (i.e., as if we were traversing
     *                         up the class hierarchy or enclosing class hierarchy); never {@code null} or empty
     * @return a {@link ContextCustomizer} or {@code null} if no customizer should
     * be used
     */
    @Override
    public ContextCustomizer createContextCustomizer(@NonNull Class<?> testClass, @Nullable List<ContextConfigurationAttributes> configAttributes) {
        return new JarvisContextCustomizer();
    }
}
