package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.ExecutionDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceScheduler;
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
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
public class JarvisServiceScheduler {

    private static final String SERVICES = JarvisServiceScheduler.class.getName() + ".services";
    private static final String EXECUTION_DESCRIPTOR = JarvisServiceScheduler.class.getName() + ".execution-descriptor";

    private final RunnerProperties properties;
    private final JarvisServiceFactory serviceFactory;
    private final ServiceScheduler serviceScheduler;

    public JarvisServiceScheduler(
            RunnerProperties properties,
            JarvisServiceFactory serviceFactory,
            ServiceScheduler serviceScheduler
    ) {
        this.properties = properties;
        this.serviceFactory = serviceFactory;
        this.serviceScheduler = serviceScheduler;
    }

    public void beforeTestClass(TestContext testContext) throws Exception {
        serviceScheduler.prepare(testContext);

        Path outputDirectory = JarvisUtils.prepareDirectory(properties.getLogsDirectory(), testContext.getTestClass());

        ExecutionDescriptor descriptor = testContext.computeAttribute(EXECUTION_DESCRIPTOR,
                n -> ExecutionDescriptor.builder()
                        .outputDirectory(outputDirectory)
                        .waitTimeout(properties.getWaitTimeout())
                        .build()
        );

        List<Service> services = testContext.computeAttribute(SERVICES, s -> getServices(testContext));

        serviceScheduler.startServices(groupByOrder(services), descriptor);
    }

    public void afterTestClass(TestContext testContext) throws Exception {
        List<Service> services = testContext.computeAttribute(SERVICES, s -> emptyList());

        ExecutionDescriptor descriptor = (ExecutionDescriptor) testContext.getAttribute(EXECUTION_DESCRIPTOR);

        serviceScheduler.stopServices(groupByOrder(services), descriptor);

        serviceScheduler.clean(testContext);
    }

    protected List<Service> getServices(TestContext testContext) {
        return MergedAnnotations.from(testContext.getTestClass()).stream(Deployment.class)
                .map(deployment -> serviceFactory.create(testContext, deployment))
                .toList();
    }

    private Stream<List<Service>> groupByOrder(List<Service> services) {
        return services.stream()
                .collect(groupingBy(Service::getOrder, TreeMap::new, toList()))
                .values().stream();
    }

}
