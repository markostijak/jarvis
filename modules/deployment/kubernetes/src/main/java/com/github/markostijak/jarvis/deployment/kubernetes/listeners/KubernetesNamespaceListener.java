package com.github.markostijak.jarvis.deployment.kubernetes.listeners;

import static com.github.markostijak.jarvis.deployment.core.internal.Labels.NAME;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.RUNNER;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.SCOPE;
import static com.github.markostijak.jarvis.deployment.kubernetes.listeners.KubernetesInitializationListener.KUBERNETES_CLIENT;
import static com.github.markostijak.jarvis.deployment.kubernetes.listeners.KubernetesInitializationListener.KUBERNETES_DEPLOYMENT_PROPERTIES;
import static com.github.markostijak.jarvis.deployment.kubernetes.utils.KubernetesResourceUtils.newNamespace;
import static com.github.markostijak.jarvis.engine.api.JarvisContext.JARVIS;
import static io.fabric8.kubernetes.client.utils.KubernetesResourceUtil.getName;

import com.github.markostijak.jarvis.deployment.core.api.Scope;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentProperties;
import com.github.markostijak.jarvis.deployment.kubernetes.utils.Kubernetes;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;

import java.util.Map;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubernetesNamespaceListener implements JarvisLifecycleListener {

    private static final String NAMESPACE = KubernetesNamespaceListener.class + ".namespace";

    private KubernetesClient client;
    private KubernetesDeploymentProperties properties;

    @Override
    public void beforeAll(JarvisContext context) {
        client = context.requireAttribute(KUBERNETES_CLIENT);
        properties = context.requireAttribute(KUBERNETES_DEPLOYMENT_PROPERTIES);

        String name = properties.getNamespace();
        if (!client.namespaces().withName(name).isReady()) {
            Namespace namespace = newNamespace(name, Map.of(
                    NAME, name,
                    RUNNER, JARVIS,
                    SCOPE, Scope.JVM.name()
            ));

            Kubernetes.apply(client, namespace);
            log.debug("Created a new namespace: '{}'", name);
            context.setAttribute(NAMESPACE, namespace);
        }
    }

    @Override
    public void afterAll(JarvisContext context) {
        Namespace namespace = (Namespace) context.removeAttribute(NAMESPACE);

        if (namespace != null) {
            Kubernetes.delete(client, namespace);
            log.debug("Deleted {} namespace", getName(namespace));
        }
    }

    @Override
    public int getOrder() {
        return 1702;
    }

}
