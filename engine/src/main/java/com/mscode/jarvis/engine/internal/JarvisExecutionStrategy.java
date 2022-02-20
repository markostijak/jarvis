package com.mscode.jarvis.engine.internal;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

@Component
@PropertySource(
        ignoreResourceNotFound = true,
        value = "classpath:junit-platform.properties"
)
public class JarvisExecutionStrategy {

    public boolean isParallelExecution(TestContext context) {
        Environment environment = context.getApplicationContext().getEnvironment();

        if (isJUnitParallelMode(environment)) {
            validateEnvironment(environment);
            validateJUnitParallelMode(environment);
            return true;
        }

        if (!"main".equals(Thread.currentThread().getName())) {
            validateEnvironment(environment);
            return true;
        }

        return false;
    }

    public String createSandbox(TestContext context, String value) {
        return value + "-" + ObjectUtils.getIdentityHexString(context);
    }

    /**
     * Checks if <code>junit.jupiter.execution.parallel.enabled</code> is set to <code>true</code>.
     */
    private boolean isJUnitParallelMode(Environment environment) {
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
