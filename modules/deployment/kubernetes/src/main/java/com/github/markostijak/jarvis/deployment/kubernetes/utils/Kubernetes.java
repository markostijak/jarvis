package com.github.markostijak.jarvis.deployment.kubernetes.utils;

import static java.util.function.Predicate.not;

import com.github.markostijak.jarvis.deployment.core.internal.Labels;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import io.fabric8.kubernetes.api.builder.Visitor;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.PrettyLoggable;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.utils.KubernetesResourceUtil;
import io.fabric8.kubernetes.client.utils.PodStatusUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings({"deprecation", "UnusedReturnValue"})
public class Kubernetes {

    public static List<HasMetadata> load(KubernetesClient client, InputStream is, Visitor<?>... customizers) {
        return client.load(is).accept(customizers).resources().map(Resource::item).toList();
    }

    public static List<HasMetadata> apply(KubernetesClient client, HasMetadata... resources) {
        return client.resourceList(resources).createOrReplace();
    }

    public static List<HasMetadata> apply(KubernetesClient client, List<HasMetadata> resources) {
        return client.resourceList(resources).createOrReplace();
    }

    public static Stream<PodResource> streamPods(KubernetesClient client, String namespace, String name) {
        return client.pods().inNamespace(namespace).withLabel(Labels.DEPLOYMENT, name).resources();
    }

    public static Stream<PodResource> streamPods(KubernetesClient client, List<HasMetadata> resources, String name) {
        return streamPods(client, KubernetesResourceUtil.getNamespace(resources.get(0)), name);
    }

    public static List<Pod> listNonTerminatingPods(KubernetesClient client, List<HasMetadata> resources, String name) {
        return streamPods(client, resources, name).map(PodResource::item).filter(not(KubernetesResourceUtils::isTerminated)).toList();
    }

    public static PrettyLoggable logs(KubernetesClient client, Pod pod, Container container) {
        return client.pods().inNamespace(pod.getMetadata().getNamespace())
                .withName(pod.getMetadata().getName())
                .inContainer(container.getName())
                .tailingLines(1000);
    }

    public static List<HasMetadata> waitUntilReady(KubernetesClient client, List<HasMetadata> resource, Duration timeout) {
        return client.resourceList(resource).waitUntilCondition(KubernetesResourceUtils::isReadyOrCompleted, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    public static void waitUntilPodIsRunning(KubernetesClient client, List<HasMetadata> resources, String name) {
        try {
            TimeUnit.SECONDS.sleep(1); // wait for kubernetes to create pods
            streamPods(client, resources, name).forEach(resource -> resource.waitUntilCondition(PodStatusUtil::isRunning, 5, TimeUnit.MINUTES));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static List<StatusDetails> delete(KubernetesClient client, HasMetadata... resources) {
        return client.resourceList(resources).delete();
    }

    public static List<StatusDetails> delete(KubernetesClient client, List<HasMetadata> resources) {
        return client.resourceList(resources).delete();
    }

    public static List<HasMetadata> get(KubernetesClient client, List<HasMetadata> resources) {
        return client.resourceList(resources).get().stream().filter(Objects::nonNull).toList();
    }
}
