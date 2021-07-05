package com.mscode.jarvis.runner.internal;

import com.mscode.jarvis.runner.DeploymentDescriptor;
import com.mscode.jarvis.runner.annotations.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;

@Slf4j
@Order(Integer.MIN_VALUE)
public class JarvisServiceScheduler implements TestExecutionListener {

    private static final String SERVICES = JarvisServiceScheduler.class.getName() + ".services";

    private final KubernetesClient k8s;
    private final ServiceFactory serviceFactory;
    private final Map<String, DeploymentDescriptor> descriptors;

    public JarvisServiceScheduler(KubernetesClient k8s,
                                  ServiceFactory serviceFactory,
                                  Map<String, DeploymentDescriptor> descriptors) {
        this.k8s = k8s;
        this.descriptors = descriptors;
        this.serviceFactory = serviceFactory;
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        List<Service> services = testContext.computeAttribute(SERVICES, s -> getServices(testContext));
        for (Service service : services) {
            log.info("Starting {} service", service.getName());
            k8s.resourceList(service.getResources()).createOrReplaceAnd().waitUntilReady(1, TimeUnit.MINUTES);
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        List<Service> services = testContext.computeAttribute(SERVICES, s -> emptyList());
        for (Service service : services) {
            log.info("Stopping {} service", service.getName());
            k8s.resourceList(service.getResources()).delete();
        }
    }

    protected List<Service> getServices(TestContext testContext) {
        List<MergedAnnotation<Deployment>> all = MergedAnnotations.from(testContext.getTestClass())
                .stream(Deployment.class).toList();

        return all.stream().map(m -> serviceFactory.create(findDescriptor(descriptors, m), m)).toList();
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
