package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.internal.utils.JarvisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Order(Integer.MIN_VALUE)
public class JarvisServiceScheduler implements TestExecutionListener {

    private static final String SERVICES = JarvisServiceScheduler.class.getName() + ".services";

    private final Path directory;
    private final JarvisServiceFactory serviceFactory;

    public JarvisServiceScheduler(JarvisServiceFactory serviceFactory, Path directory) {
        this.directory = directory;
        this.serviceFactory = serviceFactory;
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        Path outputDirectory = JarvisUtils.prepareDirectory(directory, testContext.getTestClass());
        List<Service> services = testContext.computeAttribute(SERVICES, s -> getServices(testContext));
        groupByOrder(services).map(group -> group.stream().map(service -> runAsync(() -> {
            try {
                log.info("Starting {} service", service.getName());
                service.start().waitUntilReady(1, TimeUnit.MINUTES);
                service.forwardLogsTo(outputDirectory);
            } catch (Exception e) {
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
                .map(serviceFactory::create)
                .toList();
    }

    private Stream<List<Service>> groupByOrder(List<Service> services) {
        return services.stream()
                .collect(groupingBy(Service::getOrder, TreeMap::new, toList()))
                .values().stream();
    }

}
