package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.internal.service.ExecutionParameters;
import com.mscode.jarvis.engine.internal.service.ServiceScheduler;
import com.mscode.jarvis.engine.internal.utils.JarvisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.test.context.TestContext;

import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

import static com.mscode.jarvis.engine.internal.JarvisProperties.RunnerProperties;
import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
public class JarvisServiceScheduler {

    private static final String SERVICES = JarvisServiceScheduler.class.getName() + ".services";

    private final RunnerProperties properties;
    private final ServiceScheduler serviceScheduler;
    private final JarvisServiceFactory serviceFactory;

    public JarvisServiceScheduler(JarvisServiceFactory serviceFactory, RunnerProperties properties) {
        this.properties = properties;
        this.serviceFactory = serviceFactory;
        this.serviceScheduler = null;
    }

    public void beforeTestClass(TestContext testContext) throws Exception {
        List<Service> services = testContext.computeAttribute(
                SERVICES, s -> getServices(testContext)
        );

        Path outputDirectory = JarvisUtils.prepareDirectory(
                properties.getLogsDirectory(), testContext.getTestClass()
        );

        ExecutionParameters ep = new ExecutionParameters(
                outputDirectory,
                properties.getWaitTimeout()
        );

        serviceScheduler.start(services, ep);
    }

    public void afterTestClass(TestContext testContext) throws Exception {
        List<Service> services = testContext.computeAttribute(
                SERVICES, s -> emptyList()
        );

        serviceScheduler.stop(services);
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
