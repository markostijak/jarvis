package com.github.markostijak.jarvis.deployment.core.internal;

import java.io.InputStream;
import java.util.Properties;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@RequiredArgsConstructor
public class DeploymentRegistration {

    public void register(ConfigurableEnvironment environment) throws Exception {
        // register junit properties
        Resource junitResource = new ClassPathResource("junit.properties");
        if (junitResource.exists()) {
            Properties properties = new Properties();
            try (InputStream is = junitResource.getInputStream()) {
                properties.load(is);
            }
            environment.getPropertySources().addLast(new PropertiesPropertySource("junit.properties", properties));
        }

        // register jarvisDeployments properties
        YamlPropertySourceLoader yLoader = new YamlPropertySourceLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] yResources = resolver.getResources("classpath*:/jarvis.deployment/*");
        CompositePropertySource propertySource = new CompositePropertySource("jarvis.deployments");

        for (Resource resource : yResources) {
            propertySource.getPropertySources().addAll(yLoader.load(resource.getFilename(), resource));
        }

        environment.getPropertySources().addLast(propertySource);
    }

}
