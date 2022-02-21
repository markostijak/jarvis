package com.mscode.jarvis.engine.internal.utils;

public interface UncheckedConsumer<T> {
    void accept(T t) throws Exception;
}
