package com.github.markostijak.jarvis.deployment.core.support;

import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

public class DeploymentExecutionStrategy {

    public boolean isParallelExecution(Environment environment) {
        if (isJUnitParallelMode(environment)) {
            validateEnvironment(environment);
            validateJUnitParallelMode(environment);
            return true;
        }

        return false;
    }

    /**
     * Checks if <code>junit.jupiter.execution.parallel.enabled</code> is set to <code>true</code>.
     */
    public static boolean isJUnitParallelMode(Environment environment) {
        return environment.getProperty("junit.jupiter.execution.parallel.enabled", boolean.class, false);
    }

    /**
     * Supported JUnit parallel mode:<br>
     * <code>
     * - junit.jupiter.execution.parallel.enabled = true<br>
     * - junit.jupiter.execution.parallel.mode.default = same_thread<br>
     * - junit.jupiter.execution.parallel.mode.classes.default = concurrent<br>
     * </code>
     */
    private void validateJUnitParallelMode(Environment environment) {
        Assert.state("same_thread".equals(environment.getProperty("junit.jupiter.execution.parallel.mode.default")),
                """
                        Unsupported parallel mode.
                        Supported parallel mode is:
                         junit.jupiter.execution.parallel.mode.default = same_thread
                         junit.jupiter.execution.parallel.mode.classes.default = concurrent"""
        );

        Assert.state("concurrent".equals(environment.getProperty("junit.jupiter.execution.parallel.mode.classes.default")),
                """
                        Unsupported parallel mode.
                        Supported parallel mode is:
                         junit.jupiter.execution.parallel.mode.default = same_thread
                         junit.jupiter.execution.parallel.mode.classes.default = concurrent"""
        );
    }

    private void validateEnvironment(Environment environment) {
        Assert.notNull(environment.getProperty("HOSTNAME"),
                "Parallel mode is not supported in local environment"
        );
    }

}
