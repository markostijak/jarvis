package com.github.markostijak.jarvis.deployment.core.support;

import java.util.Collections;
import java.util.Map;

import lombok.Data;

@Data
public class DeploymentProperties<T extends DeploymentDescriptor> {

    private Map<String, Deployment<T>> services = Collections.emptyMap();

    @Data
    public static class Deployment<T extends DeploymentDescriptor> {

        private T descriptor;

        public T descriptor() {
            return descriptor;
        }

        private Map<String, Object> connectionDetails;

    }

}
