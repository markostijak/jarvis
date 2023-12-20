package com.github.markostijak.jarvis.deployment.kubernetes;

import static com.github.markostijak.jarvis.deployment.core.internal.Labels.DEPLOYMENT;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.RUNNER;
import static com.github.markostijak.jarvis.deployment.core.internal.Labels.SCOPE;
import static com.github.markostijak.jarvis.deployment.kubernetes.listeners.KubernetesDeploymentListener.NAMESPACE_NAME;
import static com.github.markostijak.jarvis.engine.api.JarvisContext.JARVIS;
import static java.util.stream.Collectors.toMap;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.internal.utils.Deployments;
import com.github.markostijak.jarvis.deployment.core.internal.utils.Utils;
import com.github.markostijak.jarvis.deployment.kubernetes.utils.KubernetesUtils;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.builder.Visitor;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecBuilder;
import io.fabric8.kubernetes.client.utils.KubernetesResourceUtil;
import lombok.RequiredArgsConstructor;
import org.junit.platform.commons.util.ReflectionUtils;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class KubernetesDeploymentCustomizer implements Visitor<Object> {

    private final Deployment deployment;
    private final JarvisTestContext testContext;
    private final KubernetesDeploymentDescriptor descriptor;

    protected void customize(ObjectMetaBuilder meta) {
        meta.addToLabels(RUNNER, JARVIS);
        meta.addToLabels(DEPLOYMENT, deployment.name());
        meta.addToLabels(SCOPE, deployment.scope().name());
        meta.withNamespace((String) testContext.getAttribute(NAMESPACE_NAME));
    }

    protected void customize(ServiceSpecBuilder serviceSpec) {
        List<ServicePort> servicePorts = serviceSpec.buildPorts();
        var mapped = KubernetesUtils.parsePorts(descriptor.getPorts());

        for (ServicePort servicePort : servicePorts) {
            servicePort.setNodePort(mapped.getOrDefault(
                    servicePort.getPort(),
                    servicePort.getNodePort()
            ));
        }

        serviceSpec.withPorts(servicePorts).withType("NodePort");
    }

    protected void customize(DeploymentSpecBuilder deploymentSpec) {
        if (deploymentSpec.hasReplicas()) {
            int replicas = deploymentSpec.getReplicas();
            deploymentSpec.withReplicas(Math.min(replicas, 1));
        }
    }

    protected void customize(PodSpecBuilder podSpec) {
        List<Volume> volumes = podSpec.buildVolumes();
        var mapped = KubernetesUtils.parseVolumes(descriptor.getVolumes());

        for (Container container : podSpec.buildContainers()) {
            for (VolumeMount mount : container.getVolumeMounts()) {
                Utils.consumeIfAvailable(mapped.get(mount.getMountPath()), path -> {
                    for (Volume volume : volumes) {
                        if (volume.getName().equals(mount.getName())) {
                            volume.getHostPath().setPath(path);
                        }
                    }
                });
            }
        }

        podSpec.withVolumes(volumes);
    }

    protected void customize(ContainerBuilder container) {
        Map<String, String> envs = container.buildEnv().stream()
                .collect(toMap(EnvVar::getName, EnvVar::getValue));

        envs.putAll(Deployments.mergeEnvs(descriptor, deployment));
        container.withEnv(KubernetesResourceUtil.convertMapToEnvVarList(envs));
    }

    @Override
    public void visit(Object element) {
        ReflectionUtils.findMethod(getClass(), "customize", element.getClass())
                .ifPresent(method -> ReflectionUtils.invokeMethod(method, this, element));
    }

}
