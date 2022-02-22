package com.mscode.jarvis.engine.internal.kubernetes;

import com.mscode.jarvis.engine.api.ExecutionListener;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.createNamespace;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.namespaceExists;

@Slf4j
@Component
public class KubernetesExecutionListener implements ExecutionListener {

    private final KubernetesClient client;
    private final KubernetesProperties properties;

    private static boolean namespaceCreated = false;

    @Autowired
    public KubernetesExecutionListener(KubernetesClient client, KubernetesProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public int getOrder() {
        return -3_000;
    }

    @Override
    public void beforeAllTests(ApplicationContext context) {
        String namespace = properties.getNamespace();
        if (!namespaceExists(client, namespace)) {
            Namespace ns = createNamespace(namespace, Map.of(
                    "name", namespace,
                    "runner", "jarvis"
            ));

            client.resource(ns).createOrReplace();
            log.info("Created new {} namespace", namespace);
            namespaceCreated = true;
        }
    }

    @Override
    public void afterAllTests(ApplicationContext context) {
        if (namespaceCreated) {
            String namespace = properties.getNamespace();
            NamespaceList namespaces = client.namespaces()
                    .withLabel("name", namespace)
                    .withLabel("runner", "jarvis")
                    .list();

            client.resourceList(namespaces).delete();
            log.info("Deleted {} namespace", namespace);
        }
    }

}
