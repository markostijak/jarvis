package com.github.markostijak.jarvis.deployment.kubernetes.listeners;

import static com.github.markostijak.jarvis.deployment.core.internal.Labels.NAME;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.RUNNER;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.SCOPE;
import static com.github.markostijak.jarvis.deployment.core.internal.listeners.DeploymentListener.JARVIS_DEPLOYMENT_SCOPE;
import static com.github.markostijak.jarvis.deployment.core.internal.listeners.InitializationListener.EXECUTION_STRATEGY;
import static com.github.markostijak.jarvis.deployment.kubernetes.listeners.KubernetesInitializationListener.KUBERNETES_CLIENT;
import static com.github.markostijak.jarvis.deployment.kubernetes.listeners.KubernetesInitializationListener.KUBERNETES_DEPLOYMENT_PROPERTIES;
import static com.github.markostijak.jarvis.deployment.kubernetes.utils.KubernetesResourceUtils.newNamespace;
import static com.github.markostijak.jarvis.engine.api.JarvisContext.JARVIS;
import static com.github.markostijak.jarvis.engine.internal.JarvisLifecycleListenerAdapter.ENVIRONMENT;

import com.github.markostijak.jarvis.deployment.core.api.Scope;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentExecutionStrategy;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentProperties;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;
import com.github.markostijak.jarvis.engine.support.PropertyUtils;

import java.util.Map;
import java.util.UUID;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

@Slf4j
public class KubernetesDeploymentListener implements JarvisLifecycleListener {

    public static final String NAMESPACE = KubernetesDeploymentListener.class.getName() + ".namespace";
    public static final String NAMESPACE_NAME = KubernetesDeploymentListener.class.getName() + ".namespace.name";

    private KubernetesClient client;
    private KubernetesDeploymentProperties properties;
    private DeploymentExecutionStrategy executionStrategy;

    @Override
    public void beforeAll(JarvisContext context) {
        client = context.requireAttribute(KUBERNETES_CLIENT);
        properties = context.requireAttribute(KUBERNETES_DEPLOYMENT_PROPERTIES);
        executionStrategy = context.requireAttribute(EXECUTION_STRATEGY);
    }

    @Override
    public void beforeTestClass(JarvisTestContext testContext) {
        Environment environment = testContext.requireAttribute(ENVIRONMENT);

        String namespace = properties.getNamespace();
        Scope scope = environment.getProperty(JARVIS_DEPLOYMENT_SCOPE, Scope.class, Scope.CLASS);

        if (executionStrategy.isParallelExecution(environment)) {
            namespace = namespace + "-" + UUID.randomUUID();
            scope = Scope.CLASS; // hard override
        }

        testContext.setAttribute(NAMESPACE_NAME, namespace);
        PropertyUtils.addAsProperties(environment, Map.of(
                "jarvis.sandbox.kubernetes.namespace", namespace,
                "jarvis.sandbox.kubernetes.namespace.suffix", namespace + ".svc.cluster.local"
        ));

        if (client.namespaces().withName(namespace).isReady()) {
            return;
        }

        Namespace ns = newNamespace(namespace, Map.of(
                RUNNER, JARVIS,
                NAME, namespace,
                SCOPE, scope.name()
        ));

        ns = client.resource(ns).create();
        testContext.setAttribute(NAMESPACE, ns);
    }

    @Override
    public void afterTestClass(JarvisTestContext testContext) {
        Namespace namespace = (Namespace) testContext.removeAttribute(NAMESPACE);

        if (namespace != null) {
            client.resource(namespace).delete();
            testContext.removeAttribute(NAMESPACE_NAME);
        }
    }

    @Override
    public int getOrder() {
        return 1802;
    }

}
