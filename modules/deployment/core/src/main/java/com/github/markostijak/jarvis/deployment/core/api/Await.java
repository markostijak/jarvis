package com.github.markostijak.jarvis.deployment.core.api;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface Await {

    void waitUntilReady(Duration timeout) throws InterruptedException;

    default void waitUntilReady(long amount, TimeUnit unit) throws InterruptedException {
        waitUntilReady(Duration.of(amount, unit.toChronoUnit()));
    }

}
