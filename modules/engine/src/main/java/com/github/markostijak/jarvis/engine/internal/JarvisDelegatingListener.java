package com.github.markostijak.jarvis.engine.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.util.ReflectionUtils;

@Slf4j
@SuppressWarnings("NullableProblems")
public class JarvisDelegatingListener implements TestExecutionListener, Ordered {

    private static final String LISTENERS = JarvisDelegatingListener.class.getName() + ".listeners";
    private static final String REVERSED_LISTENERS = JarvisDelegatingListener.class.getName() + ".reversed.listeners";

    @Override
    public int getOrder() {
        return -99000;
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        for (TestExecutionListener listener : getTestExecutionListeners(testContext)) {
            try {
                listener.beforeTestClass(testContext);
            } catch (Throwable e) {
                logException(e, "beforeTestClass", listener, testContext.getTestClass());
                ReflectionUtils.rethrowException(e);
            }
        }
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        for (TestExecutionListener listener : getTestExecutionListeners(testContext)) {
            try {
                listener.prepareTestInstance(testContext);
            } catch (Throwable e) {
                logException(e, "prepareTestInstance", listener, testContext.getTestClass());
                ReflectionUtils.rethrowException(e);
            }
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        for (TestExecutionListener listener : getTestExecutionListeners(testContext)) {
            try {
                listener.beforeTestMethod(testContext);
            } catch (Throwable e) {
                logException(e, "beforeTestMethod", listener, testContext.getTestClass());
                ReflectionUtils.rethrowException(e);
            }
        }
    }

    @Override
    public void beforeTestExecution(TestContext testContext) throws Exception {
        for (TestExecutionListener listener : getTestExecutionListeners(testContext)) {
            try {
                listener.beforeTestExecution(testContext);
            } catch (Throwable e) {
                logException(e, "beforeTestExecution", listener, testContext.getTestClass());
                ReflectionUtils.rethrowException(e);
            }
        }
    }

    @Override
    public void afterTestExecution(TestContext testContext) throws Exception {
        Throwable throwable = null;

        for (TestExecutionListener listener : getReversedTestExecutionListeners(testContext)) {
            try {
                listener.afterTestExecution(testContext);
            } catch (Throwable e) {
                logException(e, "afterTestExecution", listener, testContext.getTestClass());
                if (throwable == null) {
                    throwable = e;
                } else {
                    throwable.addSuppressed(e);
                }
            }
        }

        if (throwable != null) {
            ReflectionUtils.rethrowException(throwable);
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        Throwable throwable = null;

        for (TestExecutionListener listener : getReversedTestExecutionListeners(testContext)) {
            try {
                listener.afterTestMethod(testContext);
            } catch (Throwable e) {
                logException(e, "afterTestMethod", listener, testContext.getTestClass());
                if (throwable == null) {
                    throwable = e;
                } else {
                    throwable.addSuppressed(e);
                }
            }
        }

        if (throwable != null) {
            ReflectionUtils.rethrowException(throwable);
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        Throwable throwable = null;

        for (TestExecutionListener listener : getReversedTestExecutionListeners(testContext)) {
            try {
                listener.afterTestClass(testContext);
            } catch (Throwable e) {
                logException(e, "afterTestClass", listener, testContext.getTestClass());
                if (throwable == null) {
                    throwable = e;
                } else {
                    throwable.addSuppressed(e);
                }
            }
        }

        if (throwable != null) {
            ReflectionUtils.rethrowException(throwable);
        }
    }

    private List<TestExecutionListener> getTestExecutionListeners(TestContext context) {
        return context.computeAttribute(LISTENERS, s -> {
            var listeners = context.getApplicationContext().getBeansOfType(TestExecutionListener.class)
                    .values().stream().sorted(AnnotationAwareOrderComparator.INSTANCE).toList();

            logListeners(listeners);

            return listeners;
        });
    }

    @SuppressWarnings("all")
    private List<TestExecutionListener> getReversedTestExecutionListeners(TestContext context) {
        List<TestExecutionListener> listeners = (List<TestExecutionListener>) context.getAttribute(LISTENERS);

        if (listeners == null) {
            // don't invoke 'after*' callbacks if
            // none of 'before*' callbacks are invoked
            return Collections.emptyList();
        }

        return context.computeAttribute(REVERSED_LISTENERS, s -> {
            List<TestExecutionListener> reversedListeners = new ArrayList<>(listeners);
            Collections.reverse(reversedListeners);
            return reversedListeners;
        });
    }

    private void logException(Throwable ex, String callbackName, TestExecutionListener testExecutionListener, Class<?> testClass) {
        log.warn("Caught exception while invoking '{}' callback on TestExecutionListener [{}] for test class [{}]",
                callbackName, testExecutionListener.getClass().getName(), testClass.getName(), ex);
    }

    private void logListeners(List<TestExecutionListener> listeners) {
        log.debug("Will use listeners by following order: {}", listeners.stream().map(l -> l.getClass().getSimpleName()).toList());
    }

}
