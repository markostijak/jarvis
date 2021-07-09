package com.mscode.jarvis.example;

import com.mscode.jarvis.deployment.kafka.DeployKafka;
import com.mscode.jarvis.deployment.mysql.DeployMySql;
import com.mscode.jarvis.deployment.redis.DeployRedis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@DeployRedis
@DeployKafka
@DeployMySql
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeployAll {
}
