package com.mscode.jarvis.engine.api;

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

public interface ExecutionListener extends Ordered {

    @Override
    default int getOrder() {
        return 0;
    }

    default void beforeAllTests(ApplicationContext context) throws Exception {
    }

    default void afterAllTests(ApplicationContext context) throws Exception {
    }

}
