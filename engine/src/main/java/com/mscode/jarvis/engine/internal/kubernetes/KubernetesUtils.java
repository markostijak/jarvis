package com.mscode.jarvis.engine.internal.kubernetes;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.dsl.PrettyLoggable;
import io.fabric8.kubernetes.client.internal.readiness.Readiness;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.mscode.jarvis.engine.internal.JarvisProperties.JARVIS;
import static io.fabric8.kubernetes.client.utils.PodStatusUtil.getContainerStatus;
import static java.util.Objects.nonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toCollection;
import static org.springframework.util.CollectionUtils.isEmpty;

public class KubernetesUtils {

    public static final String LABEL = JARVIS + ".deployment";

    public static List<HasMetadata> load(KubernetesClient client, String s) {
        return load(client, new ByteArrayInputStream(s.getBytes()));
    }

    public static List<HasMetadata> load(KubernetesClient client, InputStream is) {
        return client.load(is).get();
    }

    public static List<HasMetadata> loadFromYaml(KubernetesClient client, Path yaml) {
        try {
            return load(client, Files.newInputStream(yaml));
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read file " + yaml, e);
        }
    }

    public static List<Pod> listPods(KubernetesClient client, String namespace, String name) {
        return client.pods().inNamespace(namespace).withLabel(LABEL, name).list().getItems();
    }

    public static List<Pod> listNonTerminatingPods(KubernetesClient client, String namespace, String name) {
        return listPods(client, namespace, name).stream()
                .filter(not(KubernetesUtils::isTerminated))
                .toList();
    }

    public static List<Pod> listNonTerminatingPods(KubernetesClient client, List<HasMetadata> resources, String name) {
        return listPods(client, resources, name).stream()
                .filter(not(KubernetesUtils::isTerminated))
                .toList();
    }

    public static List<Pod> listPods(KubernetesClient client, List<HasMetadata> resources, String name) {
        List<Pod> pods = new ArrayList<>();

        for (HasMetadata resource : resources) {
            if (hasPodSpec(resource)) {
                ObjectMeta metadata = resource.getMetadata();
                listPods(client, metadata.getNamespace(), name).stream().collect(toCollection(() -> pods));
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

    public static PodTemplateSpec getPodTemplateSpec(HasMetadata metadata) {
        if (metadata instanceof Job job) {
            return job.getSpec().getTemplate();
        }

        if (metadata instanceof DaemonSet daemonSet) {
            return daemonSet.getSpec().getTemplate();
        }

        if (metadata instanceof CronJob cronJob) {
            return cronJob.getSpec().getJobTemplate().getSpec().getTemplate();
        }

        if (metadata instanceof Deployment deployment) {
            return deployment.getSpec().getTemplate();
        }

        return null;
    }

    public static <T> T getPodData(HasMetadata metadata, Function<PodTemplateSpec, T> mapping) {
        PodTemplateSpec templateSpec = getPodTemplateSpec(metadata);

        if (templateSpec != null) {
            return mapping.apply(templateSpec);
        }

        return null;
    }

    public static PodSpec getPodSpec(HasMetadata metadata) {
        return getPodData(metadata, PodTemplateSpec::getSpec);
    }

    public static ServiceSpec getServiceSpec(HasMetadata metadata) {
        if (metadata instanceof Service service) {
            return service.getSpec();
        }

        return null;
    }

    public static void addEnv(Container container, Map<String, String> env) {
        Map<String, String> joined = new HashMap<>();

        if (container.getEnv() != null) {
            container.getEnv().forEach(e -> joined.put(e.getName(), e.getValue()));
        }

        if (env != null) {
            joined.putAll(env);
        }

        List<EnvVar> envVars = joined.entrySet().stream()
                .map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                .toList();

        container.setEnv(envVars);
    }

    public static void addLabels(PodTemplateSpec podTemplateSpec, Map<String, String> labels) {
        Map<String, String> joined = new HashMap<>();
        ObjectMeta meta = podTemplateSpec.getMetadata();

        if (meta == null) {
            meta = new ObjectMeta();
        }

        if (meta.getLabels() != null) {
            joined.putAll(meta.getLabels());
        }

        joined.putAll(labels);

        meta.setLabels(joined);
        podTemplateSpec.setMetadata(meta);
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

    public static void replaceVolumes(List<Volume> volumes, Container container, Map<String, String> paths) {
        if (isEmpty(volumes) || isEmpty(container.getVolumeMounts())) {
            return;
        }

        paths.forEach((hostPath, path) -> container.getVolumeMounts().stream()
                .filter(vm -> vm.getMountPath().equals(path)).findFirst()
                .flatMap(vm -> volumes.stream().filter(v -> v.getName().equals(vm.getName())).findFirst())
                .filter(m -> nonNull(m.getHostPath()))
                .ifPresent(volume -> volume.getHostPath().setPath(hostPath)));
    }

    public static List<HasMetadata> convertToJob(List<HasMetadata> resources) {
        return resources.stream()
                .map(r -> r instanceof CronJob cj ? convertToJob(cj) : r)
                .toList();
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

    public static boolean isReadyOrCompleted(HasMetadata item) {
        if (item instanceof Job job) {
            Integer succeeded = job.getStatus().getSucceeded();
            return succeeded != null && succeeded == 1;
        }

        return Readiness.getInstance().isReady(item);
    }

    public static boolean isTerminated(Pod pod) {
        return getContainerStatus(pod).stream().map(ContainerStatus::getState)
                .anyMatch(state -> state != null && state.getTerminated() != null);
    }

    public static List<HasMetadata> override(List<HasMetadata> resources, KubernetesOverride override) {
        // namespace
        resources.forEach(r -> r.getMetadata().setNamespace(override.getNamespace()));

        // labels
        Map<String, String> labels = Map.of(LABEL, override.getName());
        resources.stream().map(KubernetesUtils::getPodTemplateSpec).filter(Objects::nonNull)
                .forEach(podTemplateSpec -> addLabels(podTemplateSpec, labels));

        // env
        resources.stream().map(KubernetesUtils::getPodSpec).filter(Objects::nonNull)
                .flatMap(podSpec -> podSpec.getContainers().stream())
                .forEach(container -> addEnv(container, override.getEnv()));

        // volumes
        resources.stream().map(KubernetesUtils::getPodSpec).filter(Objects::nonNull)
                .forEach(podSpec -> podSpec.getContainers()
                        .forEach(c -> replaceVolumes(podSpec.getVolumes(), c, override.getVolumes())));

        // ports
        resources.stream().map(KubernetesUtils::getServiceSpec).filter(Objects::nonNull)
                .forEach(serviceSpec -> replacePorts(serviceSpec, override.getPorts()));

        return resources;
    }

    public static Namespace createNamespace(String name, Map<String, String> labels) {
        ObjectMeta meta = new ObjectMetaBuilder()
                .withName(name)
                .withLabels(labels)
                .build();

        return new NamespaceBuilder().withMetadata(meta).build();
    }

    public static List<Namespace> listNamespaces(KubernetesClient client) {
        return client.namespaces().list().getItems();
    }

    public static boolean namespaceExists(KubernetesClient client, String name) {
        return listNamespaces(client).stream().anyMatch(n -> n.getMetadata().getName().equals(name));
    }

}
