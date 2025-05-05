package com.github.markostijak.jarvis.deployment.kubernetes.listeners;

import static com.github.markostijak.jarvis.deployment.kubernetes.listeners.KubernetesInitializationListener.KUBERNETES_CLIENT;

import com.github.markostijak.jarvis.deployment.core.internal.exceptions.JarvisDeploymentException;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubernetesAvailabilityListener implements JarvisLifecycleListener {

    @Override
    @SuppressWarnings("resource")
    public void beforeAll(JarvisContext context) {
        KubernetesClient client = context.requireAttribute(KUBERNETES_CLIENT);

        try {
            client.getKubernetesVersion();
        } catch (Exception e) {
            log.error("Kubernetes is not running. Aborting...", e);
            throw new JarvisDeploymentException("Please check if Kubernetes is up and running!", e);
        }
    }

    @Override
    public int getOrder() {
        return 1702;
    }

}
