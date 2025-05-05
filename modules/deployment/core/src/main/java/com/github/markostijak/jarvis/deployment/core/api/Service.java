package com.github.markostijak.jarvis.deployment.core.api;

public interface Service {

    Await start() throws Exception;

    boolean stop() throws Exception;

    String getName();

    int getOrder();

    boolean isRunning();

    Scope getScope();

}
