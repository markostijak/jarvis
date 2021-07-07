package com.mscode.jarvis.deployment.redis;

import com.mscode.jarvis.engine.annotation.Deployment;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deployment("redis")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration
public @interface DeployRedis {

    int order() default 0;

    boolean flushAllBeforeTest() default false;

    String[] env() default {};

}
