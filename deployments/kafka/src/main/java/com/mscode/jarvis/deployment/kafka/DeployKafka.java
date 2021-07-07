package com.mscode.jarvis.deployment.kafka;

import com.mscode.jarvis.engine.annotation.Deployment;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deployment("kafka")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration
public @interface DeployKafka {

    int delayed() default 10;

    int order() default 0;

}
