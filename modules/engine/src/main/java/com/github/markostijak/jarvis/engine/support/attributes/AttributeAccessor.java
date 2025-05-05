package com.github.markostijak.jarvis.engine.support.attributes;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public interface AttributeAccessor extends org.springframework.core.AttributeAccessor {

    @NonNull
    default <T> T requireAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return computeAttribute(name, k -> {
            throw new IllegalStateException("Missing required attribute '" + k + "'!");
        });
    }

    @NonNull
    @SuppressWarnings("unchecked")
    default <T> T getAttribute(String name, T defaultValue) {
        Assert.notNull(defaultValue, "DefaultValue must not be null");
        Object attribute = getAttribute(name);
        return attribute != null ? (T) attribute : defaultValue;
    }

    default void removeAttributes(String... names) {
        Assert.notNull(names, "You must provide at least one name");
        for (String name : names) {
            requireAttribute(name);
        }
    }

}
