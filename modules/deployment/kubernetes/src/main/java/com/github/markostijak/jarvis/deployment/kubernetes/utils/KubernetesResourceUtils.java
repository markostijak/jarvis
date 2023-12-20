package com.github.markostijak.jarvis.deployment.kubernetes.utils;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.utils.KubernetesResourceUtil;
import io.fabric8.kubernetes.client.utils.PodStatusUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class KubernetesResourceUtils {

    public static Namespace newNamespace(String name, Map<String, String> labels) {
        ObjectMeta meta = new ObjectMetaBuilder()
                .withName(name)
                .withLabels(labels)
                .build();

        return new NamespaceBuilder().withMetadata(meta).build();
    }

    public static List<HasMetadata> convertToJob(List<HasMetadata> resources) {
        return resources.stream().map(r -> r instanceof CronJob cj ? convertToJob(cj) : r).toList();
    }

    public static Job convertToJob(CronJob cronJob) {
        return new Job(cronJob.getApiVersion(), "Job", cronJob.getMetadata(),
                cronJob.getSpec().getJobTemplate().getSpec(), null);
    }

    public static boolean isReadyOrCompleted(HasMetadata item) {
        if (item instanceof Job job) {
            Integer succeeded = job.getStatus().getSucceeded();
            return succeeded != null && succeeded == 1;
        }

        return KubernetesResourceUtil.isResourceReady(item);
    }

    public static boolean isTerminated(Pod pod) {
        return PodStatusUtil.getContainerStatus(pod).stream().map(ContainerStatus::getState)
                .anyMatch(state -> state != null && state.getTerminated() != null);
    }

}
