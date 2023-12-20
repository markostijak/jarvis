package com.github.markostijak.jarvis.deployment.core.internal.listeners;

import static com.github.markostijak.jarvis.engine.internal.JarvisLifecycleListenerAdapter.ENVIRONMENT;

import com.github.markostijak.jarvis.deployment.core.internal.DefaultServiceScheduler;
import com.github.markostijak.jarvis.deployment.core.internal.DeploymentRegistration;
import com.github.markostijak.jarvis.deployment.core.internal.NoopServiceFactory;
import com.github.markostijak.jarvis.deployment.core.internal.utils.BinderUtils;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentExecutionStrategy;
import com.github.markostijak.jarvis.deployment.core.support.RunnerProperties;
import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisLifecycleListener;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

public class InitializationListener implements JarvisLifecycleListener {

    public static final String SERVICE_FACTORY = "jarvis.deployment.services.factory";
    public static final String SERVICE_SCHEDULER = "jarvis.deployment.services.scheduler";
    public static final String RUNNER_PROPERTIES = "jarvis.deployment.runner.properties";
    public static final String EXECUTION_STRATEGY = "jarvis.deployment.execution.strategy";

    @Override
    public void beforeAll(JarvisContext context) throws Exception {
        Environment environment = context.requireAttribute(ENVIRONMENT);

        RunnerProperties properties = BinderUtils.bind(environment, RunnerProperties.class);
        DeploymentRegistration deploymentRegistration = new DeploymentRegistration();
        deploymentRegistration.register((ConfigurableEnvironment) environment);

        context.setAttribute(SERVICE_FACTORY, new NoopServiceFactory());
        context.setAttribute(EXECUTION_STRATEGY, new DeploymentExecutionStrategy());
        context.setAttribute(SERVICE_SCHEDULER, new DefaultServiceScheduler(properties));
        context.setAttribute(RUNNER_PROPERTIES, properties);
    }

    @Override
    public void afterAll(JarvisContext context) {
        context.removeAttributes(SERVICE_FACTORY, SERVICE_SCHEDULER, EXECUTION_STRATEGY, RUNNER_PROPERTIES);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
