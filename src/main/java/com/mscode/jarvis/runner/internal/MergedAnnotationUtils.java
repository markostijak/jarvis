package com.mscode.jarvis.runner.internal;

import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;

import java.lang.annotation.Annotation;
import java.util.List;

public class MergedAnnotationUtils {

    public static <T extends Annotation> List<MergedAnnotation<T>> findAll(Class<?> source, Class<T> annotation) {
        return MergedAnnotations.from(source).stream(annotation).toList();
    }

    public static List<MergedAnnotation<Annotation>> findAllWith(Class<?> source, Class<? extends Annotation> annotation) {
        return MergedAnnotations.from(source, MergedAnnotations.SearchStrategy.DIRECT, RepeatableContainers.none())
                .stream().filter(m -> m.getType().isAnnotationPresent(annotation)).toList();
    }

    public static List<MergedAnnotation<Annotation>> findAllWith(Class<?> source, Class<? extends Annotation> annotation, int distance) {
        return MergedAnnotations.from(source).stream()
                .filter(m -> m.getDistance() == distance)
                .filter(m -> m.getType().isAnnotationPresent(annotation))
                .toList();
    }

}
