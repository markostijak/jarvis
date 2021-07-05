package com.mscode.jarvis.runner.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.List;

@Slf4j
public class JarvisDelegatingListener implements TestExecutionListener {

    private static final String LISTENERS = JarvisDelegatingListener.class.getName() + ".listeners";

    @Override
    public void beforeTestClass(TestContext testContext) {
        forEachListener(testContext, l -> l.beforeTestClass(testContext));
    }

    @Override
    public void prepareTestInstance(TestContext testContext) {
        forEachListener(testContext, l -> l.prepareTestInstance(testContext));
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        forEachListener(testContext, l -> l.beforeTestMethod(testContext));
    }

    @Override
    public void beforeTestExecution(TestContext testContext) {
        forEachListener(testContext, l -> l.beforeTestExecution(testContext));
    }

    @Override
    public void afterTestExecution(TestContext testContext) {
        forEachListener(testContext, l -> l.afterTestExecution(testContext));
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        forEachListener(testContext, l -> l.afterTestMethod(testContext));
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        forEachListener(testContext, l -> l.afterTestClass(testContext));
    }

    private void forEachListener(TestContext context, UncheckedConsumer<TestExecutionListener> consumer) {
        for (TestExecutionListener listener : getOrderedListeners(context)) {
            try {
                consumer.accept(listener);
            } catch (Exception e) {
                log.error("Exception occurred while executing {}", listener.getClass().getSimpleName(), e);
                throw new IllegalStateException(e);
            }
        }
    }

    private List<TestExecutionListener> getOrderedListeners(TestContext context) {
        return context.computeAttribute(LISTENERS, s -> {
            ApplicationContext applicationContext = context.getApplicationContext();
            return applicationContext.getBeansOfType(TestExecutionListener.class).values()
                    .stream().sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
        });
    }

    interface UncheckedConsumer<T> {
        void accept(T t) throws Exception;
    }

}
