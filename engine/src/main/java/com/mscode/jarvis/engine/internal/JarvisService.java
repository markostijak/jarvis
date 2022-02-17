package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import org.springframework.core.annotation.MergedAnnotation;

public abstract class JarvisService implements Service {

    private final MergedAnnotation<Deployment> annotation;

    public JarvisService(MergedAnnotation<Deployment> annotation) {
        this.annotation = annotation;
    }

    @Override
    public String getName() {
        return annotation.getString("name");
    }

    @Override
    public int getOrder() {
        return annotation.getInt("order");
    }

    @Override
    public MergedAnnotation<Deployment> getAnnotation() {
        return annotation;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name=" + getName() +
                '}';
    }

}
