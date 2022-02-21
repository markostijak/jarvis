package com.mscode.jarvis.example;

import com.mscode.jarvis.engine.api.ExecutionListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ExampleExecutionListener implements ExecutionListener {

    @Override
    public void beforeAllTests(ApplicationContext context) throws Exception {
        System.out.println("ExampleExecutionListener.beforeAllTests");
    }

    @Override
    public void afterAllTests(ApplicationContext context) throws Exception {
        System.out.println("ExampleExecutionListener.afterAllTests");
    }

}
