package com.mscode.jarvis.runner;

import com.mscode.jarvis.services.Deployment;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class JarvisExecutionListener implements TestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        List<Deployment> deployments = getDeployments(testContext.getTestClass());
    }

    private List<Deployment> getDeployments(Class<?> testClass) {
        return Arrays.stream(testClass.getAnnotations())
                .map(Annotation::annotationType)
                .filter(a -> a.isAnnotationPresent(Deployment.class))
                .map(a -> a.getAnnotation(Deployment.class))
                .toList();
    }

}
