package com.mscode.jarvis.services.redis;

import com.mscode.jarvis.runner.DeploymentDescriptor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ComponentScan
@Configuration(proxyBeanMethods = false)
public class RedisConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "jarvis.services.redis.connection")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "jarvis.services.redis.deployment")
    public DeploymentDescriptor redisDeploymentDescriptor() {
        return new DeploymentDescriptor();
    }

}
