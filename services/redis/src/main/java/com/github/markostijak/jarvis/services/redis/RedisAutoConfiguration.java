package com.github.markostijak.jarvis.services.redis;

import com.github.markostijak.jarvis.deployment.core.internal.utils.BinderUtils;
import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentProperties;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@ComponentScan
@AutoConfiguration
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnClass(DockerDeploymentProperties.class)
    public RedisPropertiesConnectionDetails dockerRedisConnectionDetails(Environment environment) {
        return new RedisPropertiesConnectionDetails(BinderUtils.bind(environment, "docker", "redis", RedisProperties.class));
    }

    @Bean
    @ConditionalOnClass(KubernetesDeploymentProperties.class)
    public RedisPropertiesConnectionDetails kubernetesRedisConnectionDetails(Environment environment) {
        return new RedisPropertiesConnectionDetails(BinderUtils.bind(environment, "kubernetes", "redis", RedisProperties.class));
    }

}
