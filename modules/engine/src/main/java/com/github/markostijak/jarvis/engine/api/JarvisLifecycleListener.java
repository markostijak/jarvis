package com.github.markostijak.jarvis.engine.api;

import org.springframework.core.Ordered;

public interface JarvisLifecycleListener extends Ordered {

    default void beforeAll(JarvisContext context) throws Exception {
    }

    default void beforeTestClass(JarvisTestContext testContext) throws Exception {
    }

    default void afterTestClass(JarvisTestContext testContext) throws Exception {
    }

    default void afterAll(JarvisContext context) throws Exception {
    }

}
