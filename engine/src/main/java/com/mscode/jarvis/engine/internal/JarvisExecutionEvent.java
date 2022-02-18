package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.api.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.test.context.TestContext;

public class JarvisExecutionEvent extends ApplicationEvent {

    public JarvisExecutionEvent(ApplicationContext context) {
        super(context);
    }

    @Override
    public final ApplicationContext getSource() {
        return (ApplicationContext) super.getSource();
    }

    public final ApplicationContext getApplicationContext() {
        return getSource();
    }

    public static abstract class ServiceEvent extends JarvisExecutionEvent {

        private final Service service;
        private final TestContext testContext;

        public ServiceEvent(TestContext testContext, Service service) {
            super(testContext.getApplicationContext());
            this.service = service;
            this.testContext = testContext;
        }

        public Service getService() {
            return service;
        }

        public TestContext getTestContext() {
            return testContext;
        }

    }

    public static class ServiceStarting extends ServiceEvent {
        public ServiceStarting(TestContext testContext, Service service) {
            super(testContext, service);
        }
    }

    public static class ServiceStarted extends ServiceEvent {
        public ServiceStarted(TestContext testContext, Service service) {
            super(testContext, service);
        }
    }

    public static class ServiceStopping extends ServiceEvent {
        public ServiceStopping(TestContext testContext, Service service) {
            super(testContext, service);
        }
    }

    public static class ServiceStopped extends ServiceEvent {
        public ServiceStopped(TestContext testContext, Service service) {
            super(testContext, service);
        }
    }

    public static class BeforeAll extends JarvisExecutionEvent {
        public BeforeAll(ApplicationContext context) {
            super(context);
        }

    }

    public static class AfterAll extends JarvisExecutionEvent {
        public AfterAll(ApplicationContext context) {
            super(context);
        }

    }

}
