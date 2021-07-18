package com.mscode.jarvis.engine.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.List;

@Slf4j
public class JarvisDelegatingListener implements TestExecutionListener {

    private static final String LISTENERS = JarvisDelegatingListener.class.getName() + ".listeners";

    @Override
    public void beforeTestClass(@NonNull TestContext testContext) throws Exception {
        getScheduler(testContext).beforeTestClass(testContext);
        forEachListener(testContext, l -> l.beforeTestClass(testContext));
    }

    @Override
    public void prepareTestInstance(@NonNull TestContext testContext) {
        forEachListener(testContext, l -> l.prepareTestInstance(testContext));
    }

    @Override
    public void beforeTestMethod(@NonNull TestContext testContext) {
        forEachListener(testContext, l -> l.beforeTestMethod(testContext));
    }

    @Override
    public void beforeTestExecution(@NonNull TestContext testContext) {
        forEachListener(testContext, l -> l.beforeTestExecution(testContext));
    }

    @Override
    public void afterTestExecution(@NonNull TestContext testContext) {
        forEachListener(testContext, l -> l.afterTestExecution(testContext));
    }

    @Override
    public void afterTestMethod(@NonNull TestContext testContext) {
        forEachListener(testContext, l -> l.afterTestMethod(testContext));
    }

    @Override
    public void afterTestClass(@NonNull TestContext testContext) throws Exception {
        try {
            forEachListener(testContext, l -> l.afterTestClass(testContext));
        } finally {
            getScheduler(testContext).afterTestClass(testContext);
        }
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
        return context.computeAttribute(LISTENERS, s -> context.getApplicationContext()
                .getBeansOfType(TestExecutionListener.class).values().stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE).toList());
    }

    private JarvisServiceScheduler getScheduler(TestContext context) {
        return context.getApplicationContext().getBean(JarvisServiceScheduler.class);
    }

    interface UncheckedConsumer<T> {
        void accept(T t) throws Exception;
    }

}
