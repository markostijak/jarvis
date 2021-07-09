package com.mscode.jarvis.engine.internal.docker;

import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Await;
import com.mscode.jarvis.engine.api.Service;
import org.springframework.core.annotation.MergedAnnotation;

import java.nio.file.Path;

public class DockerService implements Service {

    private final MergedAnnotation<Deployment> annotation;

    public DockerService(MergedAnnotation<Deployment> annotation) {
        this.annotation = annotation;
    }

    @Override
    public Await start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forwardLogsTo(Path directory) {

    }

    @Override
    public boolean stop() {
        throw new UnsupportedOperationException();
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
        return "DockerService{" +
                "name=" + getName() +
                '}';
    }

}
