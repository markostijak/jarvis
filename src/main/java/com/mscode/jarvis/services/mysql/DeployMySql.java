package com.mscode.jarvis.services.mysql;

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
@Deployment("mysql")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration
public @interface DeployMySql {

    int order() default 0;

    String[] env() default {};

}
