package com.github.markostijak.jarvis.deployment.core.internal.listeners;

import static com.github.markostijak.jarvis.deployment.core.internal.listeners.InitializationListener.SERVICE_FACTORY;
import static com.github.markostijak.jarvis.deployment.core.internal.listeners.InitializationListener.SERVICE_SCHEDULER;
import static com.github.markostijak.jarvis.engine.internal.JarvisLifecycleListenerAdapter.BEAN_FACTORY;
import static com.github.markostijak.jarvis.engine.internal.JarvisLifecycleListenerAdapter.ENVIRONMENT;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Service;
import com.github.markostijak.jarvis.deployment.core.api.ServiceFactory;
import com.github.markostijak.jarvis.deployment.core.api.ServiceScheduler;
import com.github.markostijak.jarvis.deployment.core.internal.utils.Deployments;
import com.github.markostijak.jarvis.deployment.core.internal.utils.Utils;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentBean;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.env.Environment;

@Slf4j
public class DeploymentListener implements JarvisLifecycleListener {

    public static final String JVM_SCOPED_SERVICES = "jarvis.services.scoped.jvm";
    public static final String CLASS_SCOPED_SERVICES = "jarvis.services.scoped.class";
    public static final String PACKAGE_SCOPED_SERVICES = "jarvis.services.scoped.package";
    public static final String JARVIS_DEPLOYMENT_SCOPE = "jarvis.deployment.scope";

    private ServiceFactory serviceFactory;
    private ServiceScheduler serviceScheduler;

    @Override
    public void beforeAll(JarvisContext context) {
        serviceFactory = context.requireAttribute(SERVICE_FACTORY);
        serviceScheduler = context.requireAttribute(SERVICE_SCHEDULER);
    }

    @Override
    public void beforeTestClass(JarvisTestContext testContext) {
        JarvisContext context = testContext.getParent();
        Environment environment = testContext.requireAttribute(ENVIRONMENT);
        ListableBeanFactory beanFactory = testContext.requireAttribute(BEAN_FACTORY);

        List<Deployment> deployments = new ArrayList<>();
        deployments.addAll(Deployments.list(testContext.getTestClass(), environment)); // from test class annotations
        deployments.addAll(beanFactory.getBeansOfType(DeploymentBean.class).values()); // from spring context

        Set<Service> services = new HashSet<>(deployments.size());

        for (Deployment deployment : deployments) {
            Service service = serviceFactory.create(testContext, deployment);

            Set<Service> scoped = switch (service.getScope()) {
                case JVM -> context.computeAttribute(JVM_SCOPED_SERVICES, s -> new HashSet<>());
                case CLASS, DEFAULT -> testContext.computeAttribute(CLASS_SCOPED_SERVICES, s -> new HashSet<>());
                case PACKAGE -> context.computeAttribute(PACKAGE_SCOPED_SERVICES, s -> new HashMap<String, Set<Service>>()
                        .computeIfAbsent(testContext.getTestClass().getPackageName(), p -> new HashSet<>()));
            };

            if (scoped.add(service)) {
                services.add(service);
            }
        }

        if (!services.isEmpty()) {
            serviceScheduler.deploy(services);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterTestClass(JarvisTestContext testContext) {
        Set<Service> services = new HashSet<>(Utils.orElse(
                (Set<Service>) testContext.removeAttribute(CLASS_SCOPED_SERVICES), emptySet()
        ));

        Map<String, Set<Service>> scoped = testContext.getAttribute(PACKAGE_SCOPED_SERVICES, emptyMap());

        Set<String> forRemoval = new HashSet<>(scoped.size());
        scoped.forEach((packageName, packageScopeServices) -> {
            if (!testContext.getTestClass().getPackageName().equals(packageName)) {
                services.addAll(packageScopeServices);
                forRemoval.add(packageName);
            }
        });

        forRemoval.forEach(scoped::remove);

        if (!services.isEmpty()) {
            serviceScheduler.destroy(services);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterAll(JarvisContext jarvisContext) {
        Set<Service> services = new HashSet<>();

        Utils.consumeIfAvailable((Map<String, Set<Service>>) jarvisContext.removeAttribute(PACKAGE_SCOPED_SERVICES),
                scoped -> scoped.forEach((packageName, packageScopeServices) -> services.addAll(packageScopeServices)));

        Utils.consumeIfAvailable((Set<Service>) jarvisContext.removeAttribute(JVM_SCOPED_SERVICES), services::addAll);

        if (!services.isEmpty()) {
            serviceScheduler.destroy(services);
        }
    }

    @Override
    public int getOrder() {
        return 2000;
    }

}
