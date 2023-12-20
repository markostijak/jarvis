package com.github.markostijak.jarvis.deployment.core.internal;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.github.markostijak.jarvis.deployment.core.api.Service;
import com.github.markostijak.jarvis.deployment.core.api.ServiceScheduler;
import com.github.markostijak.jarvis.deployment.core.internal.exceptions.JarvisDeploymentException;
import com.github.markostijak.jarvis.deployment.core.support.RunnerProperties;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DefaultServiceScheduler implements ServiceScheduler {

    private final RunnerProperties properties;

    public boolean deploy(Collection<Service> services) {
        AtomicBoolean result = new AtomicBoolean(true);

        Stream<List<Service>> batches = services.stream()
                .collect(groupingBy(Service::getOrder, TreeMap::new, toList()))
                .values().stream();

        batches.map(batch -> batch.stream().map(service -> runAsync(() -> {
            try {
                log.info("Starting {} service", service.getName());
                service.start().waitUntilReady(properties.getWaitTimeout());
            } catch (Exception e) {
                result.set(false);
                log.error("Service {} readiness failed!", service.getName(), e);
                throw new JarvisDeploymentException("Service " + service.getName() + " failed!", e);
            }
        })).toArray(CompletableFuture[]::new)).forEach(batch -> allOf(batch).join());

        return result.get();
    }

    public boolean destroy(Collection<Service> services) {
        boolean result = true;
        Exception exception = null;

        for (Service service : services) {
            log.info("Stopping {} service", service.getName());
            if (service.isRunning()) {
                try {
                    result = result && service.stop();
                } catch (Exception e) {
                    log.warn("Unable to stop {} service!", service.getName(), exception);
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            } else {
                log.info("Service {} is not running", service.getName());
            }
        }

        if (exception != null) {
            throw new JarvisDeploymentException("Unable to stop all services!", exception);
        }

        return result;
    }

}
