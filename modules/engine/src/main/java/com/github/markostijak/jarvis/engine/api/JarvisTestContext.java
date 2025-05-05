package com.github.markostijak.jarvis.engine.api;

import com.github.markostijak.jarvis.engine.support.attributes.AttributeAccessor;

import org.springframework.lang.NonNull;

public interface JarvisTestContext extends AttributeAccessor {

    @NonNull
    Class<?> getTestClass();

    @NonNull
    JarvisContext getParent();

}
