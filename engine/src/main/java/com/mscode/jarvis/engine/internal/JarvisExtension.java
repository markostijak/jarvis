package com.mscode.jarvis.engine.internal;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class JarvisExtension extends SpringExtension implements ExtensionContext.Store.CloseableResource {

    private static final String IDENTIFIER = JarvisExtension.class.getName();

    private static final AtomicBoolean started = new AtomicBoolean();

    @Override
    public void beforeAll(@NonNull ExtensionContext context) throws Exception {
        synchronized (IDENTIFIER) {
            if (started.compareAndSet(false, true)) {
                context.getStore(GLOBAL).put(IDENTIFIER, this);
                ApplicationContext applicationContext = getApplicationContext(context);
                publishEvent(applicationContext, new JarvisExecutionEvent.BeforeAll(applicationContext));
            }
        }

        super.beforeAll(context);
    }

    @Override
    public void close() {
        ApplicationContext applicationContext = getApplicationContext(null /*extensionContext*/);
        publishEvent(applicationContext, new JarvisExecutionEvent.AfterAll(applicationContext));
    }

    private void publishEvent(ApplicationContext applicationContext, ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

}
