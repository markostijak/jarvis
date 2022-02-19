package com.mscode.jarvis.engine.internal.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionParameters {

    private Path outputDirectory;

    private Duration waitTimeout;

}
