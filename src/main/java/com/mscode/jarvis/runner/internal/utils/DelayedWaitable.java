package com.mscode.jarvis.runner.internal.utils;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.dsl.Waitable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class DelayedWaitable implements Waitable<List<HasMetadata>, HasMetadata> {

    private final Duration delay;
    private final List<HasMetadata> hasMetadata;

    public DelayedWaitable(int seconds, List<HasMetadata> hasMetadata) {
        this.delay = Duration.ofSeconds(seconds);
        this.hasMetadata = hasMetadata;
    }

    @Override
    public List<HasMetadata> waitUntilReady(long amount, TimeUnit timeUnit) throws InterruptedException {
        Thread.sleep(Math.min(delay.toMillis(), timeUnit.toMillis(amount)));

        return hasMetadata;
    }

    @Override
    public List<HasMetadata> waitUntilCondition(Predicate<HasMetadata> condition, long amount, TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Waitable<List<HasMetadata>, HasMetadata> withWaitRetryBackoff(long a, TimeUnit tu, double bm) {
        throw new UnsupportedOperationException();
    }

}
