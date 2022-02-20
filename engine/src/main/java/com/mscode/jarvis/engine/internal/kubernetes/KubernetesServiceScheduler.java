package com.mscode.jarvis.engine.internal.kubernetes;

import com.mscode.jarvis.engine.ExecutionDescriptor;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceScheduler;
import com.mscode.jarvis.engine.internal.JarvisExecutionStrategy;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.createNamespace;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.namespaceExists;
import static com.mscode.jarvis.engine.internal.utils.PropertyUtils.addAsProperties;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
@Component
public class KubernetesServiceScheduler implements ServiceScheduler {

    public static final String NAMESPACE = KubernetesServiceScheduler.class.getName() + ".namespace";
    public static final String NAMESPACE_NAME = KubernetesServiceScheduler.class.getName() + ".namespace.name";

    private final KubernetesClient client;
    private final KubernetesProperties properties;
    private final JarvisExecutionStrategy executionStrategy;

    @Autowired
    public KubernetesServiceScheduler(
            KubernetesClient client,
            KubernetesProperties properties,
            JarvisExecutionStrategy executionStrategy
    ) {
        this.client = client;
        this.properties = properties;
        this.executionStrategy = executionStrategy;
    }

    @Override
    public void prepare(TestContext context) {
        String namespace = properties.getNamespace();

        if (executionStrategy.isParallelExecution(context)) {
            namespace = executionStrategy.createSandbox(context, namespace);
            context.setAttribute(NAMESPACE_NAME, namespace);
        }

        addAsProperties(context, Map.of(
                "jarvis.kubernetes.namespace", namespace,
                "jarvis.kubernetes.namespace.suffix", namespace + ".svc.cluster.local"
        ));

        if (namespaceExists(client, namespace)) {
            return;
        }

        Namespace ns = createNamespace(namespace, Map.of(
                "name", namespace,
                "testClass", context.getTestClass().getName()
        ));

        ns = client.resource(ns).createOrReplace();
        context.setAttribute(NAMESPACE, ns);
    }

    @Override
    public void startServices(Stream<List<Service>> services, ExecutionDescriptor descriptor) {
        services.map(batch -> batch.stream().map(service -> runAsync(() -> {
            try {
                log.info("Starting {} service", service.getName());
                service.start().waitUntilReady(descriptor.getWaitTimeout());
                service.forwardLogsTo(descriptor.getOutputDirectory());
            } catch (Exception e) {
                throw new CompletionException("Service " + service.getName() + " readiness failed!", e);
            }
        })).toArray(CompletableFuture[]::new)).forEach(batch -> allOf(batch).join());
    }

    @Override
    public void stopServices(Stream<List<Service>> services, ExecutionDescriptor descriptor) {
        services.flatMap(Collection::stream).forEach(service -> {
            log.info("Stopping {} service", service.getName());
            service.stop();
        });
    }

    @Override
    public void clean(TestContext context) {
        Namespace ns = (Namespace) context.getAttribute(NAMESPACE);

        if (ns != null) {
            client.resource(ns).delete();
        }
    }

}
