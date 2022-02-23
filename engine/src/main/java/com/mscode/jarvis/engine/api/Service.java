package com.mscode.jarvis.engine.api;


import com.mscode.jarvis.engine.annotation.Deployment;
import org.springframework.core.annotation.MergedAnnotation;

import java.io.IOException;
import java.nio.file.Path;

public interface Service {

    Await start();

    boolean stop();

    String getName();

    int getOrder();

    void forwardLogsTo(Path directory) throws IOException;

    MergedAnnotation<Deployment> getAnnotation();

    boolean isDeployed();

}
