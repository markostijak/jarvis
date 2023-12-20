package com.github.markostijak.jarvis.services.kafka;

import com.github.markostijak.jarvis.deployment.core.annotations.Deploy;
import com.github.markostijak.jarvis.deployment.core.api.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

@Documented
@Deploy("kafka")
@Deploy("zookeeper")
@Deploy("schema-registry")
@ImportAutoConfiguration
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeployKafka {

    int order() default 0;

    String[] env() default {};

    String delayed() default "";

    Scope scope() default Scope.DEFAULT;

}
