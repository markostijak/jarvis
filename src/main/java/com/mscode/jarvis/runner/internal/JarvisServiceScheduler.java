package com.mscode.jarvis.runner.internal;

import com.mscode.jarvis.runner.DeploymentDescriptor;
import com.mscode.jarvis.runner.annotations.Deployment;
import com.mscode.jarvis.runner.internal.utils.ServiceUtils;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.List;
import java.util.Map;
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

    private final KubernetesClient client;
    private final ServiceFactory serviceFactory;
    private final Map<String, DeploymentDescriptor> descriptors;

    public JarvisServiceScheduler(KubernetesClient client,
                                  ServiceFactory serviceFactory,
                                  Map<String, DeploymentDescriptor> descriptors) {
        this.client = client;
        this.descriptors = descriptors;
        this.serviceFactory = serviceFactory;
    }

    @Override
    public void beforeTestClass(TestContext testContext) {
        List<Service> services = testContext.computeAttribute(SERVICES, s -> getServices(testContext));
        ServiceUtils.groupByOrder(services).stream().map(group -> group.stream().map(service -> runAsync(() -> {
            try {
                log.info("Starting {} service", service.getName());
                service.start(client).waitUntilReady(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new CompletionException("Service " + service.getName() + " is not ready!", e);
            }
        })).toArray(CompletableFuture[]::new)).forEach(array -> allOf(array).join());
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        List<Service> services = testContext.computeAttribute(SERVICES, s -> emptyList());
        for (Service service : services) {
            log.info("Stopping {} service", service.getName());
            service.stop(client);
        }
    }

    protected List<Service> getServices(TestContext testContext) {
        List<MergedAnnotation<Deployment>> all = MergedAnnotations.from(testContext.getTestClass())
                .stream(Deployment.class).toList();

        return all.stream().filter(annotation -> annotation.hasNonDefaultValue("name"))
                .map(annotation -> serviceFactory.create(findDescriptor(descriptors, annotation), annotation)).toList();
    }

    private DeploymentDescriptor findDescriptor(Map<String, DeploymentDescriptor> descriptors, MergedAnnotation<Deployment> annotation) {
        String name = annotation.getString("name");
        DeploymentDescriptor descriptor = descriptors.get(name);

        if (descriptor == null) {
            log.warn("Unable to find deployment descriptor for {} service. Using empty one!", name);
            return DeploymentDescriptor.empty();
        }

        return descriptor;
    }

}
