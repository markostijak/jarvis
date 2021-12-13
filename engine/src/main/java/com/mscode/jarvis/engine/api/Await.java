package com.mscode.jarvis.engine.api;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface Await {

    void waitUntilReady(long amount, TimeUnit unit) throws InterruptedException;

    default void waitUntilReady(Duration duration) throws InterruptedException {
        waitUntilReady(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

}
