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
        return findByName(name).orElseThrow(() -> new NoSuchElementException(name + " deployment descriptor"));
    }

}
