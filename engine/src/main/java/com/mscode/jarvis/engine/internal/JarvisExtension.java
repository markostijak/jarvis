package com.mscode.jarvis.engine.internal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class JarvisExtension {

    private static final AtomicLong counter = new AtomicLong();

    @EventListener
    public void beforeAll(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        if (context.getParent() == null && counter.getAndIncrement() == 0) {
            context.publishEvent(new JarvisExecutionEvent.BeforeAll(context));
        }
    }

    @EventListener
    public void afterAll(ContextClosedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        if (context.getParent() == null && counter.decrementAndGet() == 0) {
            context.publishEvent(new JarvisExecutionEvent.AfterAll(context));
        }
    }

}
