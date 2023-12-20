package com.github.markostijak.jarvis.deployment.kubernetes.listeners;

import static com.github.markostijak.jarvis.deployment.core.internal.listeners.InitializationListener.SERVICE_FACTORY;
import static com.github.markostijak.jarvis.engine.internal.JarvisLifecycleListenerAdapter.ENVIRONMENT;

import com.github.markostijak.jarvis.deployment.core.internal.utils.BinderUtils;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentProperties.Deployment;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentRepository;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentDescriptor;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentProperties;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesResourceLoader;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesServiceFactory;
import com.github.markostijak.jarvis.deployment.kubernetes.helm.Helm;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;

import java.util.Collections;
import java.util.Map;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.core.env.Environment;

public class KubernetesInitializationListener implements JarvisLifecycleListener {

    public static final String KUBERNETES_CLIENT = "kubernetes.client";
    public static final String KUBERNETES_DEPLOYMENT_PROPERTIES = "kubernetes.deployment.properties";

    @Override
    public void beforeAll(JarvisContext context) {
        Environment environment = context.requireAttribute(ENVIRONMENT);
        KubernetesClient client = new KubernetesClientBuilder().build();
        KubernetesDeploymentProperties deploymentProperties = BinderUtils.bind(environment, KubernetesDeploymentProperties.class);

        Map<String, Deployment<KubernetesDeploymentDescriptor>> services = deploymentProperties.getServices();
        DeploymentRepository<KubernetesDeploymentDescriptor> deploymentRepository = new DeploymentRepository<>(
                Collections.unmodifiableMap(services)
        );

        KubernetesResourceLoader resourceLoader = new KubernetesResourceLoader(Helm.getInstance(), deploymentProperties, deploymentRepository);
        KubernetesServiceFactory serviceFactory = new KubernetesServiceFactory(client, resourceLoader, deploymentRepository, deploymentProperties);

        context.setAttribute(KUBERNETES_CLIENT, client);
        context.setAttribute(SERVICE_FACTORY, serviceFactory);
        context.setAttribute(KUBERNETES_DEPLOYMENT_PROPERTIES, deploymentProperties);
    }

    @Override
    public void afterAll(JarvisContext context) {
        KubernetesClient client = (KubernetesClient) context.removeAttribute(KUBERNETES_CLIENT);

        if (client != null) {
            client.close();
        }
    }

    @Override
    public int getOrder() {
        return 102;
    }

}
