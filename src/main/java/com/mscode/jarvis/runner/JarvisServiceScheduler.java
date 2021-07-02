package com.mscode.jarvis.runner;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

@Component
@Order(Integer.MIN_VALUE)
public class JarvisServiceScheduler implements TestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        // start services
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        // destroy services
    }

}
