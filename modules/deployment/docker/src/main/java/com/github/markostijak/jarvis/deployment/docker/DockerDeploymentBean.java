package com.github.markostijak.jarvis.deployment.docker;

import com.github.markostijak.jarvis.deployment.core.api.Scope;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DockerDeploymentBean extends DeploymentBean {

    private final DockerDeploymentDescriptor descriptor;

    DockerDeploymentBean(DockerDeploymentDescriptor descriptor, Integer order, Scope scope, String delayed) {
        super(descriptor.getContainerName(), scope, order, delayed);
        this.descriptor = descriptor;
    }

    public DockerDeploymentDescriptor descriptor() {
        return descriptor;
    }

    public static DockerDeploymentBeanBuilder builder() {
        return new DockerDeploymentBeanBuilder();
    }

    public static class DockerDeploymentBeanBuilder {

        private String image;
        private String containerName;
        private Integer order;
        private String delayed;
        private Scope scope;
        private List<String> ports;
        private List<String> volumes;
        private Map<String, String> envs;

        public DockerDeploymentBeanBuilder image(String image) {
            this.image = image;
            return this;
        }

        public DockerDeploymentBeanBuilder containerName(String containerName) {
            this.containerName = containerName;
            return this;
        }

        public DockerDeploymentBeanBuilder order(Integer order) {
            this.order = order;
            return this;
        }

        public DockerDeploymentBeanBuilder scope(Scope scope) {
            this.scope = scope;
            return this;
        }

        public DockerDeploymentBeanBuilder delayed(String delayed) {
            this.delayed = delayed;
            return this;
        }

        public DockerDeploymentBeanBuilder port(String portBinding) {
            return ports(portBinding);
        }

        public DockerDeploymentBeanBuilder ports(String... portBindings) {
            if (ports == null) {
                ports = new ArrayList<>();
            }

            Collections.addAll(ports, portBindings);
            return this;
        }

        public DockerDeploymentBeanBuilder volume(String volumeBinding) {
            return volumes(volumeBinding);
        }

        public DockerDeploymentBeanBuilder volumes(String... volumeBindings) {
            if (volumes == null) {
                volumes = new ArrayList<>();
            }

            Collections.addAll(volumes, volumeBindings);
            return this;
        }

        public DockerDeploymentBeanBuilder env(String name, String value) {
            return envs(Map.of(name, value));
        }

        public DockerDeploymentBeanBuilder envs(Map<String, String> variables) {
            if (envs == null) {
                envs = new HashMap<>();
            }

            envs.putAll(variables);
            return this;
        }

        public DockerDeploymentBean build() {
            Objects.requireNonNull(image);
            Objects.requireNonNull(containerName);

            var descriptor = new DockerDeploymentDescriptor();
            descriptor.setImage(image);
            descriptor.setEnv(envs);
            descriptor.setPorts(ports);
            descriptor.setVolumes(volumes);
            descriptor.setContainerName(containerName);

            return new DockerDeploymentBean(descriptor, order, scope, delayed);
        }
    }

}
