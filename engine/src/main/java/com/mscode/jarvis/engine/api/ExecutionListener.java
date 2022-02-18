package com.mscode.jarvis.engine.api;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;

public interface ExecutionListener {

    default void beforeAll(ApplicationContext context) {
        // no-op
    }

    default void onServiceStarting(TestContext testContext, Service service) {
        // no-op
    }

    default void onServiceStarted(TestContext testContext, Service service) {
        // no-op
    }

    default void onServiceStopping(TestContext testContext, Service service) {
        // no-op
    }

    default void onServiceStopped(TestContext testContext, Service service) {
        // no-op
    }

    default void afterAll(ApplicationContext context) {
        // no-op
    }

}
