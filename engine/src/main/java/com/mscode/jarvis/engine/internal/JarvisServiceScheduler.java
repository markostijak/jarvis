package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.internal.utils.JarvisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
@Order(Integer.MIN_VALUE)
public class JarvisServiceScheduler implements TestExecutionListener {

    private static final String SERVICES = JarvisServiceScheduler.class.getName() + ".services";

    private final JarvisServiceFactory serviceFactory;

    public JarvisServiceScheduler(JarvisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public void beforeTestClass(TestContext testContext) {
        List<Service> services = testContext.computeAttribute(SERVICES, s -> getServices(testContext));
        groupByOrder(services).stream().map(group -> group.stream().map(service -> runAsync(() -> {
            try {
                log.info("Starting {} service", service.getName());
                service.start().waitUntilReady(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new CompletionException("Service " + service.getName() + " readiness failed!", e);
            }
        })).toArray(CompletableFuture[]::new)).forEach(array -> allOf(array).join());
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        List<Service> services = testContext.computeAttribute(SERVICES, s -> emptyList());
        for (Service service : services) {
            log.info("Stopping {} service", service.getName());
            service.stop();
        }
    }

    protected List<Service> getServices(TestContext testContext) {
        return MergedAnnotations.from(testContext.getTestClass()).stream(Deployment.class)
                .map(serviceFactory::create).toList();
    }

    private List<List<Service>> groupByOrder(List<Service> services) {
        return List.of(services);
    }

}
