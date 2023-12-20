package com.github.markostijak.jarvis.runner;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClasspathRoots;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

@Slf4j
public class JarvisRunner {

    public static void main(String[] args) {
        if (args.length != 0) {
            run(requireNonNull(args[0], "Missing class or package"));
        } else {
            log.debug("Scanning classpath for test classes");
            Set<Path> paths = Stream.of(System.getProperty("java.class.path").split(";"))
                    .map(Path::of).collect(toSet());

            run(selectClasspathRoots(paths).toArray(DiscoverySelector[]::new));
        }
    }

    public static void run(DiscoverySelector... selectors) {
        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectors)
                .build();

        LauncherConfig config = LauncherConfig.builder()
                .addTestExecutionListeners(listener)
                .build();

        LauncherFactory.create(config).execute(request);

        StringWriter out = new StringWriter();
        StringWriter error = new StringWriter();

        TestExecutionSummary summary = listener.getSummary();
        summary.printTo(new PrintWriter(out));
        summary.printFailuresTo(new PrintWriter(error), 25);

        log.info("Test execution report\n" + out);
        if (!error.toString().isEmpty()) {
            log.error("Test execution failures\n" + error);
        }
    }

    public static void run(String classOrPackage) {
        boolean isClass = classOrPackage.chars().anyMatch(Character::isUpperCase);
        run(isClass ? selectClass(classOrPackage) : selectPackage(classOrPackage));
    }
}
