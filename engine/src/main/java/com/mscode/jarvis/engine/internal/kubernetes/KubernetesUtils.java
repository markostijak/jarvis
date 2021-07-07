package com.mscode.jarvis.engine.internal.kubernetes;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class KubernetesUtils {

    public static List<HasMetadata> loadFromYaml(KubernetesClient client, Path yaml) {
        try {
            return client.load(Files.newInputStream(yaml)).get();
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read file " + yaml, e);
        }
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

}
