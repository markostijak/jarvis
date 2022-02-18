package com.mscode.jarvis.example;

import com.mscode.jarvis.engine.api.ExecutionListener;
import com.mscode.jarvis.engine.api.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;

@Component
public class JarvisExecutionListener implements ExecutionListener {

    @Override
    public void beforeAll(ApplicationContext context) {
        System.out.println("JarvisExecutionListener.beforeAll");
    }

    @Override
    public void onServiceStarting(TestContext testContext, Service service) {
        System.out.println("JarvisExecutionListener.onServiceStarting");
    }

    @Override
    public void onServiceStarted(TestContext testContext, Service service) {
        System.out.println("JarvisExecutionListener.onServiceStarted");
    }

    @Override
    public void onServiceStopping(TestContext testContext, Service service) {
        System.out.println("JarvisExecutionListener.onServiceStopping");
    }

    @Override
    public void onServiceStopped(TestContext testContext, Service service) {
        System.out.println("JarvisExecutionListener.onServiceStopped");
    }

    @Override
    public void afterAll(ApplicationContext context) {
        System.out.println("JarvisExecutionListener.afterAll");
    }

}
