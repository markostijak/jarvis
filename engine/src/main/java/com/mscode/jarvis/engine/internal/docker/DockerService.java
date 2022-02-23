package com.mscode.jarvis.engine.internal.docker;

import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Await;
import com.mscode.jarvis.engine.internal.service.AbstractService;
import org.springframework.core.annotation.MergedAnnotation;

import java.nio.file.Path;

public class DockerService extends AbstractService {

    public DockerService(MergedAnnotation<Deployment> annotation) {
        super(annotation);
    }

    @Override
    public Await start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stop() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forwardLogsTo(Path directory) {

    }

    @Override
    public boolean isDeployed() {
        return false;
    }

}
