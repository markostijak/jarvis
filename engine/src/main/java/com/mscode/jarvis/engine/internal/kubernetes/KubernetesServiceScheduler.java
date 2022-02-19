package com.mscode.jarvis.engine.internal.kubernetes;

import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.internal.service.ExecutionParameters;
import com.mscode.jarvis.engine.internal.service.ServiceScheduler;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;

import java.util.List;
import java.util.Map;

import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.createNamespace;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.namespaceExists;

@Slf4j
@Component
public class KubernetesServiceScheduler implements ServiceScheduler {

    private static final String NAMESPACE = KubernetesServiceScheduler.class.getName() + ".namespace.";

    private final KubernetesClient client;
    private final KubernetesProperties properties;

    @Autowired
    public KubernetesServiceScheduler(KubernetesClient client, KubernetesProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public void prepare(TestContext context) {
        String name = properties.getNamespace() + "-" + Thread.currentThread().getId();

        // do not create/delete existing namespaces
        if (namespaceExists(client, name)) {
            return;
        }

        Namespace namespace = context.computeAttribute(NAMESPACE + name,
                n -> createNamespace(name, Map.of(
                        "name", name,
                        "testClass", context.getTestClass().getName()
                ))
        );

        client.resource(namespace).createOrReplace();
    }

    @Override
    public void start(List<Service> services, ExecutionParameters environment) {

    }

    @Override
    public void stop(List<Service> services) {

    }

    @Override
    public void clean(TestContext context) {
        String name = properties.getNamespace() + "-" + Thread.currentThread().getId();

        Namespace namespace = (Namespace) context.getAttribute(NAMESPACE + name);

        if (namespace != null) {
            client.resource(namespace).delete();
        }
    }

}
