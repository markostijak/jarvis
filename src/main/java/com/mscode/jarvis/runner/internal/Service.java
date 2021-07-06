package com.mscode.jarvis.runner.internal;

import com.mscode.jarvis.runner.DeploymentDescriptor;
import com.mscode.jarvis.runner.annotations.Deployment;
import com.mscode.jarvis.runner.internal.utils.DelayedWaitable;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Waitable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.core.annotation.MergedAnnotation;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@ToString(of = "name")
@EqualsAndHashCode(of = "name")
public class Service {

    private final String name;
    private final DeploymentDescriptor descriptor;
    private final List<HasMetadata> resources;
    private final MergedAnnotation<Deployment> annotation;

    public Waitable<List<HasMetadata>, HasMetadata> start(KubernetesClient client) {
        Integer delayed = Optional.ofNullable(annotation.getMetaSource())
                .flatMap(a -> a.getValue("delayed", Integer.class))
                .orElse(null);

        if (delayed != null && delayed > 0) {
            return new DelayedWaitable(delayed, client.resourceList(resources).createOrReplace());
        }

        return client.resourceList(resources).createOrReplaceAnd();
    }

    public Boolean stop(KubernetesClient client) {
        return client.resourceList(resources).delete();
    }

}
