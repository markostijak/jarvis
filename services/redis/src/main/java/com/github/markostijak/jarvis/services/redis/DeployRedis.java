package com.github.markostijak.jarvis.services.redis;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.core.annotation.AliasFor;

@Documented
@ImportAutoConfiguration
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Deployment("redis")
public @interface DeployRedis {

    @AliasFor(annotation = Deployment.class)
    int order() default 0;

    @AliasFor(annotation = Deployment.class)
    String[] env() default {};

    @AliasFor(annotation = Deployment.class)
    String delayed() default "";

    @AliasFor(annotation = Deployment.class)
    Scope scope() default Scope.DEFAULT;

}
