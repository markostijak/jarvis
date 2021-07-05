package com.mscode.jarvis.services;

import com.mscode.jarvis.services.kafka.DeployKafka;
import com.mscode.jarvis.services.mysql.DeployMySql;
import com.mscode.jarvis.services.redis.DeployRedis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DeployMySql
@DeployRedis
@DeployKafka
public @interface DeployAll {
}
