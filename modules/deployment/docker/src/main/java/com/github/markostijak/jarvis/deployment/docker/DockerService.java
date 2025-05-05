package com.github.markostijak.jarvis.deployment.docker;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Await;
import com.github.markostijak.jarvis.deployment.core.support.AbstractService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.convert.DurationStyle;
import org.testcontainers.containers.GenericContainer;

public class DockerService extends AbstractService {

    private final Path logDirectory;
    private final GenericContainer<?> container;

    private PrintWriter logWriter;

    public DockerService(GenericContainer<?> container, Deployment deployment, Path logDirectory) {
        super(deployment);
        this.container = container;
        this.logDirectory = logDirectory;
    }

    @Override
    public Await start() throws IOException {
        Path logFile = logDirectory.resolve(getName() + ".log");
        long delayed = DurationStyle.SIMPLE.parse(deployment.delayed()).toMillis();
        logWriter = new PrintWriter(Files.newBufferedWriter(logFile, CREATE, APPEND), true);

        container.withLogConsumer(frame -> logWriter.println(frame.getUtf8StringWithoutLineEnding()))
                .start();

        return timeout -> Thread.sleep(Math.min(timeout.toMillis(), delayed));
    }

    @Override
    public boolean stop() {
        // stop container
        container.stop();

        // flush logs
        logWriter.flush();
        logWriter.close();

        return true;
    }

    @Override
    public boolean isRunning() {
        return container.isRunning();
    }

}
