package com.mscode.jarvis.engine.internal.utils;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.util.Assert;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toMap;

public class JarvisUtils {

    public static Path prepareDirectory(Path directory, Class<?> clazz) throws IOException {
        Path target = directory.resolve(clazz.getName());

        if (Files.exists(target)) {
            FileSystemUtils.deleteRecursively(target);
        }

        return Files.createDirectories(target);
    }

    public static Map<String, String> mergeEnv(DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        Map<String, String> env = new HashMap<>(descriptor.getEnv());

        if (deployment.hasNonDefaultValue("env")) {
            String[] envs = deployment.getStringArray("env");
            for (String e : envs) {
                String[] parts = e.split("=");
                Assert.state(parts.length % 2 == 0, "Envs must be defined in 'key=value' format!");
                env.put(parts[0], parts[1]);
            }
        }

        return env;
    }

    public static Map<Integer, Integer> parsePorts(DeploymentDescriptor descriptor) {
        return descriptor.getPorts().stream().map(p -> p.split(":"))
                .collect(toMap(p -> Integer.parseInt(p[0]), p -> Integer.parseInt(p[1])));
    }

    public static void waitForDelay(long delay, long amount, TimeUnit timeUnit) throws InterruptedException {
        TimeUnit.SECONDS.sleep(Math.min(timeUnit.toSeconds(amount), delay));
    }

}
