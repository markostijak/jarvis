package com.mscode.jarvis.runner.internal;

import com.mscode.jarvis.runner.DeploymentDescriptor;
import com.mscode.jarvis.runner.annotations.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mscode.jarvis.runner.internal.MergedAnnotationUtils.findAll;
import static java.util.Collections.emptyList;

@Slf4j
@Order(Integer.MIN_VALUE)
public class JarvisServiceScheduler implements TestExecutionListener {

    private static final String SERVICES = JarvisServiceScheduler.class.getName() + ".services";

    private final KubernetesClient k8s;
    private final ServiceFactory serviceFactory;

    @Autowired
    public JarvisServiceScheduler(KubernetesClient k8s, ServiceFactory serviceFactory) {
        this.k8s = k8s;
        this.serviceFactory = serviceFactory;
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        List<Service> services = testContext.computeAttribute(SERVICES, s -> getServices(testContext));
        for (Service service : services) {
            k8s.resourceList(service.getResources()).createOrReplaceAnd().waitUntilReady(1, TimeUnit.MINUTES);
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        List<Service> services = testContext.computeAttribute(SERVICES, s -> emptyList());
        for (Service service : services) {
            k8s.resourceList(service.getResources()).delete();
        }
    }

    private List<Service> getServices(TestContext testContext) {
        ApplicationContext context = testContext.getApplicationContext();
        Map<String, DeploymentDescriptor> deployments = context.getBeansOfType(DeploymentDescriptor.class);
        List<MergedAnnotation<Deployment>> all = findAll(testContext.getTestClass(), Deployment.class);

        return all.stream().map(m -> createService(deployments, m)).toList();
    }

    private Service createService(Map<String, DeploymentDescriptor> descriptors, MergedAnnotation<Deployment> deployment) {
        String deploymentName = deployment.getString("name");
        DeploymentDescriptor deploymentDescriptor = findDeploymentDescriptor(deploymentName, descriptors);
        return serviceFactory.create(deploymentDescriptor, deployment);
    }

    private DeploymentDescriptor findDeploymentDescriptor(String name, Map<String, DeploymentDescriptor> deployments) {
        return deployments.entrySet().stream().filter(e -> e.getKey().toLowerCase().startsWith(name.toLowerCase()))
                .findFirst().map(Map.Entry::getValue).orElse(DeploymentDescriptor.empty());
    }

}
