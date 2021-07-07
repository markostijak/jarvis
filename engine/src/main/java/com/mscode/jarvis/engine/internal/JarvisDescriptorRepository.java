package com.mscode.jarvis.engine.internal;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
public class JarvisDescriptorRepository {

    private final Map<String, DeploymentDescriptor> descriptors;

    public JarvisDescriptorRepository(Map<String, DeploymentDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    public Optional<DeploymentDescriptor> findByName(String name) {
        return Optional.ofNullable(descriptors.get(name));
    }

    public DeploymentDescriptor getByName(String name) throws NoSuchElementException {
        DeploymentDescriptor descriptor = descriptors.get(name);

        if (descriptor == null) {
            throw new NoSuchElementException(name + " deployment descriptor not found");
        }

        return descriptor;
    }

    public DeploymentDescriptor getByName(String name, DeploymentDescriptor defaultValue) {
        DeploymentDescriptor descriptor = descriptors.get(name);

        if (descriptor != null) {
            return descriptor;
        }

        log.warn("Unable to find {} deployment descriptor. Using default!", name);
        return defaultValue;
    }

    public DeploymentDescriptor getByNameOrEmpty(String name) {
        DeploymentDescriptor descriptor = descriptors.get(name);

        if (descriptor != null) {
            return descriptor;
        }

        log.warn("Unable to find {} deployment descriptor. Using default!", name);
        return DeploymentDescriptor.empty();
    }

}
