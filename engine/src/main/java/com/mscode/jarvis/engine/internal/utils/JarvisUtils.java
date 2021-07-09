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

public class JarvisUtils {

    public static Path prepareDirectory(Path directory, Class<?> clazz) throws IOException {
        Path target = directory.resolve(clazz.getName());

        if (Files.exists(directory)) {
            FileSystemUtils.deleteRecursively(directory);
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

}
