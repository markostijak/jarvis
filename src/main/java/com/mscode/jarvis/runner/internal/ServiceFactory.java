package com.mscode.jarvis.runner.internal;

import com.mscode.jarvis.runner.DeploymentDescriptor;
import com.mscode.jarvis.runner.annotations.Deployment;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.core.annotation.MergedAnnotation;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceFactory {

    private final Path basePath;
    private final KubernetesClient client;

    public ServiceFactory(Path basePath, KubernetesClient client) {
        this.basePath = basePath;
        this.client = client;
    }

    public Service create(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        MergedAnnotation<?> annotation = deployment.getMetaSource();

        if (annotation == null) {
            throw new IllegalStateException("Deployment should be used on another annotation only!");
        }

        List<EnvVar> env = mergeEnv(descriptor, annotation);
        String name = deployment.getString("name");
        List<HasMetadata> resources = createResources(descriptor);

        for (HasMetadata resource : resources) {
            PodSpec podSpec = getPodSpec(resource);
            if (podSpec != null) {
                for (Container container : podSpec.getContainers()) {
                    container.setEnv(mergeEnv(container.getEnv(), env));
                }
            }
        }

        return new Service(name, resources);
    }

    protected List<HasMetadata> createResources(DeploymentDescriptor descriptor) {
        List<HasMetadata> resources = new ArrayList<>();
        for (Path path : descriptor.getPaths()) {
            Path yaml = basePath.resolve(path);
            try {
                resources.addAll(client.load(Files.newInputStream(yaml)).get());
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to read file " + yaml, e);
            }
        }
        return resources;
    }

    protected static List<EnvVar> mergeEnv(DeploymentDescriptor descriptor, MergedAnnotation<?> annotation) {
        Map<String, String> env = new HashMap<>(descriptor.getEnv());

        if (annotation != null && annotation.hasNonDefaultValue("env")) {
            String[] envs = annotation.getStringArray("env");
            for (String e : envs) {
                String[] parts = e.split("=");
                env.put(parts[0], parts[1]);
            }
        }

        return env.entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null)).toList();
    }

    private static List<EnvVar> mergeEnv(List<EnvVar> existing, List<EnvVar> additional) {
        List<EnvVar> env = new ArrayList<>();

        if (existing != null) {
            env.addAll(existing);
        }

        if (additional != null) {
            env.addAll(additional);
        }

        return env;
    }

    protected static PodSpec getPodSpec(HasMetadata metadata) {
        if (metadata instanceof PodSpec podSpec) {
            return podSpec;
        }

        if (metadata instanceof Job job) {
            return job.getSpec().getTemplate().getSpec();
        }

        if (metadata instanceof DaemonSet daemonSet) {
            return daemonSet.getSpec().getTemplate().getSpec();
        }

        if (metadata instanceof CronJob cronJob) {
            return cronJob.getSpec().getJobTemplate().getSpec().getTemplate().getSpec();
        }

        if (metadata instanceof io.fabric8.kubernetes.api.model.apps.Deployment deployment) {
            return deployment.getSpec().getTemplate().getSpec();
        }

        return null;
    }

}
