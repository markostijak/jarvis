package com.github.markostijak.jarvis.engine.internal;

import static java.util.Collections.emptyList;

import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;
import com.github.markostijak.jarvis.engine.internal.exceptions.JarvisExecutionException;
import com.github.markostijak.jarvis.engine.support.attributes.ConcurrentAttributeAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Slf4j
@Getter
@SuppressWarnings("NullableProblems")
public class JarvisExecutionContext extends ConcurrentAttributeAccessor implements JarvisContext {

    private List<JarvisLifecycleListener> lifecycleListeners = emptyList();
    private List<JarvisLifecycleListener> reversedLifecycleListeners = emptyList();

    public void registerLifecycleListeners(List<JarvisLifecycleListener> lifecycleListeners) {
        List<JarvisLifecycleListener> reversedLifecycleListeners = new ArrayList<>(lifecycleListeners);
        Collections.reverse(reversedLifecycleListeners);

        this.lifecycleListeners = Collections.unmodifiableList(lifecycleListeners);
        this.reversedLifecycleListeners = Collections.unmodifiableList(reversedLifecycleListeners);
    }

    public void beforeAll() throws JarvisExecutionException {
        for (JarvisLifecycleListener listener : lifecycleListeners) {
            try {
                listener.beforeAll(this);
            } catch (Throwable e) {
                log.warn("Caught exception while invoking 'beforeAll' callback on JarvisLifecycleListener [{}]", listener.getClass().getName());
                throw new JarvisExecutionException(listener.getClass().getSimpleName() + ".beforeAll callback failed: [" + e.getMessage() + "]", e);
            }
        }
    }

    public void beforeTestClass(JarvisTestContext testContext) throws JarvisExecutionException {
        for (JarvisLifecycleListener listener : lifecycleListeners) {
            try {
                listener.beforeTestClass(testContext);
            } catch (Throwable e) {
                log.warn("Caught exception while invoking 'beforeTestClass' callback on JarvisLifecycleListener [{}]", listener.getClass().getName());
                throw new JarvisExecutionException(listener.getClass().getSimpleName() + ".beforeTestClass callback failed: [" + e.getMessage() + "]", e);
            }
        }
    }

    public void afterTestClass(JarvisTestContext testContext) throws JarvisExecutionException {
        Exception exception = null;

        for (JarvisLifecycleListener listener : reversedLifecycleListeners) {
            try {
                listener.afterTestClass(testContext);
            } catch (Exception e) {
                log.warn("Caught exception while invoking 'afterTestClass' callback on JarvisLifecycleListener [{}]", listener.getClass().getName());
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }

        if (exception != null) {
            throw new JarvisExecutionException("One or more 'JarvisLifecycleListener.afterTestClass' callbacks failed: [" + exception.getMessage() + "]", exception);
        }
    }

    public void afterAll() throws JarvisExecutionException {
        Exception exception = null;

        for (JarvisLifecycleListener listener : reversedLifecycleListeners) {
            try {
                listener.afterAll(this);
            } catch (Exception e) {
                log.warn("Caught exception while invoking 'afterAll' callback on JarvisLifecycleListener [{}]", listener.getClass().getName());
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }

        if (exception != null) {
            throw new JarvisExecutionException("One or more 'JarvisLifecycleListener.afterAll' callbacks failed: [" + exception.getMessage() + "]", exception);
        }
    }

    @Override
    public void setAttribute(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        synchronized (this.attributes) {
            if (value != null) {
                this.attributes.put(name, value);
            } else {
                this.attributes.remove(name);
            }
        }
    }

}
