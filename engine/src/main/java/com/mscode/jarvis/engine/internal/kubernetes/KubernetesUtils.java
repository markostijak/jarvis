package com.mscode.jarvis.engine.internal.kubernetes;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.dsl.PrettyLoggable;
import io.fabric8.kubernetes.client.internal.readiness.Readiness;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class KubernetesUtils {

    public static List<HasMetadata> loadFromYaml(KubernetesClient client, Path yaml) {
        try {
            return client.load(Files.newInputStream(yaml)).get();
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read file " + yaml, e);
        }
    }

    public static List<Pod> listPods(KubernetesClient client, String namespace, String name) {
        return client.pods().inNamespace(namespace).withLabel("app", name).list().getItems();
    }

    public static List<Pod> listPods(KubernetesClient client, List<HasMetadata> resources) {
        List<Pod> pods = new ArrayList<>();

        for (HasMetadata resource : resources) {
            if (hasPodSpec(resource)) {
                ObjectMeta metadata = resource.getMetadata();
                pods.addAll(listPods(client, metadata.getNamespace(), metadata.getName()));
            }
        }

        return pods;
    }

    public static PrettyLoggable<LogWatch> logs(KubernetesClient client, Pod pod, Container container) {
        return client.pods().inNamespace(pod.getMetadata().getNamespace())
                .withName(pod.getMetadata().getName())
                .inContainer(container.getName())
                .tailingLines(1000);
    }

    public static boolean hasPodSpec(HasMetadata metadata) {
        return getPodSpec(metadata) != null;
    }

    public static PodSpec getPodSpec(HasMetadata metadata) {
        if (metadata instanceof Job job) {
            return job.getSpec().getTemplate().getSpec();
        }

        if (metadata instanceof DaemonSet daemonSet) {
            return daemonSet.getSpec().getTemplate().getSpec();
        }

        if (metadata instanceof CronJob cronJob) {
            return cronJob.getSpec().getJobTemplate().getSpec().getTemplate().getSpec();
        }

        if (metadata instanceof Deployment deployment) {
            return deployment.getSpec().getTemplate().getSpec();
        }

        return null;
    }

    public static ServiceSpec getServiceSpec(HasMetadata metadata) {
        if (metadata instanceof Service service) {
            return service.getSpec();
        }

        return null;
    }

    public static void addEnv(Container container, Map<String, String> env) {
        List<EnvVar> additional = env.entrySet().stream()
                .map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                .toList();

        List<EnvVar> envVars = Stream.of(container.getEnv(), additional)
                .filter(Objects::nonNull).flatMap(Collection::stream)
                .toList();

        container.setEnv(envVars);
    }

    public static void replacePorts(ServiceSpec serviceSpec, Map<Integer, Integer> ports) {
        ports.forEach((nodePort, port) -> {
            for (ServicePort servicePort : serviceSpec.getPorts()) {
                if (servicePort.getPort().equals(port)) {
                    servicePort.setNodePort(nodePort);
                }
            }
        });
    }

    public static Job convertToJob(CronJob cronJob) {
        return new Job(
                cronJob.getApiVersion(),
                "Job",
                cronJob.getMetadata(),
                cronJob.getSpec().getJobTemplate().getSpec(),
                null
        );
    }

    public static void waitUntilReadyOrCompleted(KubernetesClient client, List<HasMetadata> created, long amount, TimeUnit timeUnit) throws InterruptedException {
        client.resourceList(created).waitUntilCondition(item -> {
            if (item instanceof Job job) {
                Integer succeeded = job.getStatus().getSucceeded();
                return succeeded != null && succeeded == 1;
            }

            return Readiness.getInstance().isReady(item);
        }, amount, timeUnit);
    }

}
