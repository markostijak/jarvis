package com.github.markostijak.jarvis.engine.internal;

import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

@RequiredArgsConstructor
public class JarvisLifecycleListenerAdapter implements TestExecutionListener, BeanFactoryPostProcessor, Ordered {

    public static final String ENVIRONMENT = "spring.environment";
    public static final String BEAN_FACTORY = "spring.bean.factory";

    private static final String PREFIX = JarvisLifecycleListenerAdapter.class.getName();

    private final JarvisExecutionContext executionContext;
    private final JarvisTestClassScopedContext testClassContext;

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Environment environment = beanFactory.getBean(Environment.class);

        executionContext.computeAttribute(ENVIRONMENT, k -> environment); // only once

        final AtomicBoolean started = executionContext.computeAttribute(PREFIX + ".beforeAll", k -> new AtomicBoolean(false));

        synchronized (executionContext) {
            if (!started.getAndSet(true)) {
                executionContext.beforeAll(); // only once
            }
        }

        testClassContext.setAttribute(ENVIRONMENT, environment);
        testClassContext.setAttribute(BEAN_FACTORY, beanFactory);

        executionContext.beforeTestClass(testClassContext);
    }

    @Override
    public void beforeTestClass(TestContext springContext) {
        springContext.setAttribute(JarvisTestContext.class.getName(), testClassContext);
    }

    @Override
    public void afterTestClass(@NonNull TestContext springContext) {
        try {
            executionContext.afterTestClass(testClassContext);
        } finally {
            testClassContext.removeAllAttributes();
            springContext.removeAttribute(JarvisTestContext.class.getName());
        }
    }

    public void registerShutdownHook() {
        executionContext.computeAttribute(PREFIX + ".shutdownHook", k -> {
            Runtime.getRuntime().addShutdownHook(new Thread(executionContext::afterAll)); // only once
            return true;
        });
    }

    @Override
    public int getOrder() {
        return 130000;
    }

}
