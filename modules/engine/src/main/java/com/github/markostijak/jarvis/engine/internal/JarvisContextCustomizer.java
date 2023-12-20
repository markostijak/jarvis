package com.github.markostijak.jarvis.engine.internal;

import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

@Slf4j
@RequiredArgsConstructor
public class JarvisContextCustomizer implements ContextCustomizer {
    /**
     * Customize the supplied {@code ConfigurableApplicationContext} <em>after</em>
     * bean definitions have been loaded into the context but <em>before</em> the
     * context has been refreshed.
     *
     * @param applicationContext the context to customize
     * @param mergedConfig       the merged context configuration
     */
    @Override
    public void customizeContext(@NonNull ConfigurableApplicationContext applicationContext, @NonNull MergedContextConfiguration mergedConfig) {
        Class<?> testClass = mergedConfig.getTestClass();

        JarvisExecutionContextInitializer initializer = new JarvisExecutionContextInitializer();
        JarvisExecutionContext jarvisExecutionContext = initializer.initialize(applicationContext);

        JarvisTestClassScopedContext jarvisTestContext = new JarvisTestClassScopedContext(testClass, jarvisExecutionContext);
        JarvisLifecycleListenerAdapter lifecycleListenerAdapter = new JarvisLifecycleListenerAdapter(jarvisExecutionContext, jarvisTestContext);

        applicationContext.getBeanFactory().registerSingleton(JarvisContext.class.getName(), jarvisExecutionContext);
        applicationContext.getBeanFactory().registerSingleton(JarvisTestContext.class.getName(), jarvisTestContext);
        applicationContext.getBeanFactory().registerSingleton(JarvisLifecycleListenerAdapter.class.getName(), lifecycleListenerAdapter);
        lifecycleListenerAdapter.registerShutdownHook();
    }

}
