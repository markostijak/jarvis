package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.api.ExecutionListener;
import com.mscode.jarvis.engine.internal.utils.UncheckedConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class JarvisExecutionListenerAdapter {

    public static final AtomicInteger counter = new AtomicInteger();

    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();

        synchronized (counter) {
            if (counter.getAndIncrement() == 0 && context.getParent() == null) {
                forEachListener(context, l -> l.beforeAllTests(context));
            }
        }
    }

    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        ApplicationContext context = event.getApplicationContext();

        if (counter.decrementAndGet() == 0 && context.getParent() == null) {
            forEachListener(context, l -> l.afterAllTests(context));
        }
    }

    private void forEachListener(ApplicationContext context, UncheckedConsumer<ExecutionListener> consumer) {
        for (ExecutionListener listener : getOrderedListeners(context)) {
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
