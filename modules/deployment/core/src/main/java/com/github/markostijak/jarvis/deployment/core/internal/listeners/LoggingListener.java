package com.github.markostijak.jarvis.deployment.core.internal.listeners;

import static com.github.markostijak.jarvis.deployment.core.internal.listeners.InitializationListener.RUNNER_PROPERTIES;

import com.github.markostijak.jarvis.deployment.core.api.Scope;
import com.github.markostijak.jarvis.deployment.core.support.RunnerProperties;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.util.FileSystemUtils;

@Slf4j
public class LoggingListener implements JarvisLifecycleListener, TestExecutionListener {

    public static final String LOG_DIRECTORY = LoggingListener.class.getName() + ".directory";

    private RunnerProperties properties;

    @Override
    public void beforeAll(@NonNull JarvisContext context) throws Exception {
        properties = context.requireAttribute(RUNNER_PROPERTIES);

        Path logsDirectory = properties.getLogsDirectory();

        if (Files.exists(logsDirectory)) {
            try (Stream<Path> files = Files.list(logsDirectory)) {
                files.filter(Files::isDirectory).forEach(d -> {
                    try {
                        FileSystemUtils.deleteRecursively(d);
                    } catch (Exception e) {
                        log.warn("Unable to delete {} directory", d, e);
                    }
                });
            }
        } else {
            Files.createDirectories(logsDirectory);
        }

        context.setAttribute(LOG_DIRECTORY, logsDirectory);
    }

    @Override
    public void beforeTestClass(@NonNull JarvisTestContext testContext) throws Exception {
        Path jvmDirectory = properties.getLogsDirectory();
        Path packageDirectory = jvmDirectory.resolve(testContext.getTestClass().getPackageName());
        Path classDirectory = packageDirectory.resolve(testContext.getTestClass().getSimpleName());

        List<Path> directories = List.of(
                jvmDirectory,
                packageDirectory,
                classDirectory
        );

        for (Path directory : directories) {
            if (!Files.exists(directory)) {
                Files.createDirectory(directory);
            }
        }

        testContext.setAttribute(LOG_DIRECTORY, Map.of(
                Scope.JVM, jvmDirectory,
                Scope.PACKAGE, packageDirectory,
                Scope.CLASS, classDirectory
        ));
    }

    @Override
    public void afterTestClass(JarvisTestContext testContext) {
        testContext.removeAttribute(LOG_DIRECTORY);
    }

    @Override
    public int getOrder() {
        return 1000;
    }

}
