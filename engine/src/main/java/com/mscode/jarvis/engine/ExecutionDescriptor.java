package com.mscode.jarvis.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.time.Duration;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionDescriptor {

    private Path outputDirectory;

    private Duration waitTimeout;

}
