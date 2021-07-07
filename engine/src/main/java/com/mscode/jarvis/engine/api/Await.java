package com.mscode.jarvis.engine.api;

import java.util.concurrent.TimeUnit;

public interface Await {

    void waitUntilReady(long amount, TimeUnit unit) throws InterruptedException;

}
