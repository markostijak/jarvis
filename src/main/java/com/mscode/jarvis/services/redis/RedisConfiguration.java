package com.mscode.jarvis.services.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = "com.mscode.jarvis.services.redis")
public class RedisConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "jarvis.services.redis")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

}
