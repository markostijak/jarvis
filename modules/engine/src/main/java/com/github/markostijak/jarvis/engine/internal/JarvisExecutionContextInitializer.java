package com.github.markostijak.jarvis.engine.internal;

import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.util.TestContextSpringFactoriesUtils;

@Slf4j
public class JarvisExecutionContextInitializer {

    private static final Object lock = new Object();

    private static JarvisExecutionContext jarvisExecutionContext;

    public JarvisExecutionContext initialize(ConfigurableApplicationContext context) {
        synchronized (lock) {
            if (jarvisExecutionContext == null) {
                jarvisExecutionContext = new JarvisExecutionContext();
                List<JarvisLifecycleListener> listeners = TestContextSpringFactoriesUtils.loadFactoryImplementations(JarvisLifecycleListener.class);
                jarvisExecutionContext.registerLifecycleListeners(listeners);
                log.debug("Created JarvisContext");
            } else {
                log.debug("Reusing JarvisContext");
            }
        }

        logListeners(jarvisExecutionContext.getLifecycleListeners());

        return jarvisExecutionContext;
    }

    private void logListeners(List<JarvisLifecycleListener> listeners) {
        log.debug("Will use lifecycle listeners by following order: {}",
                listeners.stream().map(l -> l.getClass().getSimpleName()).toList());
    }

}
