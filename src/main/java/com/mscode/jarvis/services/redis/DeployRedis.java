package com.mscode.jarvis.services.redis;

import com.mscode.jarvis.runner.annotations.Deployment;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Deployment("redis")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration
public @interface DeployRedis {

    int order() default 0;

    boolean flushAllBeforeTest() default false;

    String[] env() default {};

}
