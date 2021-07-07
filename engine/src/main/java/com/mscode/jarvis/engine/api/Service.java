package com.mscode.jarvis.engine.api;


import com.mscode.jarvis.engine.annotation.Deployment;
import org.springframework.core.annotation.MergedAnnotation;

public interface Service {

    Await start();

    boolean stop();

    String getName();

    MergedAnnotation<Deployment> getAnnotation();

}
