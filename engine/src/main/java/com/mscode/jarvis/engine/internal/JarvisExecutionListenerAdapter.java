package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.api.ExecutionListener;
import com.mscode.jarvis.engine.internal.utils.UncheckedConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class JarvisExecutionListenerAdapter {

    @EventListener
    public void beforeAll(JarvisExecutionEvent.BeforeAll event) {
        forEachListener(event, l -> l.beforeAll(event.getApplicationContext()));
    }

    @EventListener
    public void onServiceStarting(JarvisExecutionEvent.ServiceStarting event) {
        forEachListener(event, l -> l.onServiceStarting(event.getTestContext(), event.getService()));
    }

    @EventListener
    public void onServiceStarted(JarvisExecutionEvent.ServiceStarted event) {
        forEachListener(event, l -> l.onServiceStarted(event.getTestContext(), event.getService()));
    }

    @EventListener
    public void onServiceStopping(JarvisExecutionEvent.ServiceStopping event) {
        forEachListener(event, l -> l.onServiceStopping(event.getTestContext(), event.getService()));
    }

    @EventListener
    public void onServiceStopped(JarvisExecutionEvent.ServiceStopped event) {
        forEachListener(event, l -> l.onServiceStopped(event.getTestContext(), event.getService()));
    }

    @EventListener
    public void afterAll(JarvisExecutionEvent.AfterAll event) {
        forEachListener(event, l -> l.beforeAll(event.getApplicationContext()));
    }

    private void forEachListener(JarvisExecutionEvent event, UncheckedConsumer<ExecutionListener> consumer) {
        for (ExecutionListener listener : getOrderedListeners(event.getApplicationContext())) {
            try {
                consumer.accept(listener);
            } catch (Exception e) {
                log.error("Exception occurred while executing {}", listener.getClass().getSimpleName(), e);
                throw new IllegalStateException(e);
            }
        }
    }

    private List<ExecutionListener> getOrderedListeners(ApplicationContext context) {
        return context.getBeansOfType(ExecutionListener.class).values().stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
    }

}
