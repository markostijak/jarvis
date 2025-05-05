package com.github.markostijak.jarvis.deployment.kubernetes;

import com.github.markostijak.jarvis.deployment.core.api.Scope;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentBean;
import com.github.markostijak.jarvis.deployment.kubernetes.helm.HelmChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KubernetesDeploymentBean extends DeploymentBean {

    private final KubernetesDeploymentDescriptor descriptor;

    KubernetesDeploymentBean(KubernetesDeploymentDescriptor d, Integer order, Scope scope, String delayed) {
        super(d.getYaml() != null ? d.getYaml() : d.getHelm().getChart(), scope, order, delayed);
        this.descriptor = d;
    }

    public KubernetesDeploymentDescriptor descriptor() {
        return descriptor;
    }

    public static KubernetesDeploymentBeanBuilder fromYaml(String yaml) {
        return new KubernetesDeploymentBeanBuilder(yaml);
    }

    public static KubernetesDeploymentBeanBuilder fromHelm(HelmChart chart) {
        return new KubernetesDeploymentBeanBuilder(chart);
    }

    public static class KubernetesDeploymentBeanBuilder {

        private String yaml;
        private HelmChart helmChart;
        private Integer order;
        private String delayed;
        private Scope scope;
        private List<String> ports;
        private List<String> volumes;
        private Map<String, String> envs;

        public KubernetesDeploymentBeanBuilder(String yaml) {
            this.yaml = Objects.requireNonNull(yaml);
        }

        public KubernetesDeploymentBeanBuilder(HelmChart helmChart) {
            this.helmChart = Objects.requireNonNull(helmChart);
        }

        public KubernetesDeploymentBeanBuilder order(Integer order) {
            this.order = order;
            return this;
        }

        public KubernetesDeploymentBeanBuilder scope(Scope scope) {
            this.scope = scope;
            return this;
        }

        public KubernetesDeploymentBeanBuilder delayed(String delayed) {
            this.delayed = delayed;
            return this;
        }

        public KubernetesDeploymentBeanBuilder port(String portBinding) {
            return ports(portBinding);
        }

        public KubernetesDeploymentBeanBuilder ports(String... portBindings) {
            if (ports == null) {
                ports = new ArrayList<>();
            }

            Collections.addAll(ports, portBindings);
            return this;
        }

        public KubernetesDeploymentBeanBuilder volume(String volumeBinding) {
            return volumes(volumeBinding);
        }

        public KubernetesDeploymentBeanBuilder volumes(String... volumeBindings) {
            if (volumes == null) {
                volumes = new ArrayList<>();
            }

            Collections.addAll(volumes, volumeBindings);
            return this;
        }

        public KubernetesDeploymentBeanBuilder env(String name, String value) {
            return envs(Map.of(name, value));
        }

        public KubernetesDeploymentBeanBuilder envs(Map<String, String> variables) {
            if (envs == null) {
                envs = new HashMap<>();
            }

            envs.putAll(variables);
            return this;
        }

        public KubernetesDeploymentBean build() {
            var descriptor = new KubernetesDeploymentDescriptor();
            descriptor.setYaml(yaml);
            descriptor.setEnv(envs);
            descriptor.setPorts(ports);
            descriptor.setVolumes(volumes);
            descriptor.setHelm(helmChart);

            return new KubernetesDeploymentBean(descriptor, order, scope, delayed);
        }

    }

}
