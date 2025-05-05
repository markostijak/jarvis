package com.github.markostijak.jarvis.deployment.core.support;

import com.github.markostijak.jarvis.deployment.core.internal.exceptions.DeploymentDescriptorException;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentProperties.Deployment;

import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class DeploymentRepository<T extends DeploymentDescriptor> {

    private final Map<String, Deployment<? extends DeploymentDescriptor>> deployments;

    public Optional<Deployment<T>> findByName(String name) {
        return Optional.ofNullable((Deployment<T>) deployments.get(name));
    }

    public Deployment<T> getByName(String name) throws DeploymentDescriptorException {
        return findByName(name).orElseThrow(() -> new DeploymentDescriptorException("'" + name + "' deployment descriptor is missing"));
    }

}
