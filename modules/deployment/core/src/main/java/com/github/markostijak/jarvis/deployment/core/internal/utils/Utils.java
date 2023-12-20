package com.github.markostijak.jarvis.deployment.core.internal.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    @SafeVarargs
    public static <K, V> Map<K, V> merge(Map<K, V>... maps) {
        Map<K, V> combined = new LinkedHashMap<>();

        for (Map<K, V> map : maps) {
            combined.putAll(map);
        }

        return combined;
    }

    @SafeVarargs
    public static <T> T firstNonNull(T... targets) {
        for (T target : targets) {
            if (target != null) {
                return target;
            }
        }

        return null;
    }

    public static <T> T orElse(T target, T other) {
        return target == null ? other : target;
    }

    public static <T> T orElseGet(T target, Supplier<T> supplier) {
        return target != null ? target : supplier.get();
    }

    public static <T, R> R mapOrElse(T target, Function<T, R> mapping, R other) {
        return target == null ? other : mapping.apply(target);
    }

    public static <T> T mapOrElseGet(T target, Function<T, T> mapping, Supplier<T> supplier) {
        return target != null ? mapping.apply(target) : supplier.get();
    }

    public static <T> void consumeIfAvailable(T target, Consumer<T> consumer) {
        if (target != null) {
            consumer.accept(target);
        }
    }

    public static <T> void consumeIfAvailable(Collection<T> collection, Consumer<Collection<T>> consumer) {
        if (collection != null && !collection.isEmpty()) {
            consumer.accept(collection);
        }
    }

}
