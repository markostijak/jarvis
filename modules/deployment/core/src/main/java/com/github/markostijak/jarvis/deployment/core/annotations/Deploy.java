package com.github.markostijak.jarvis.deployment.core.annotations;

import com.github.markostijak.jarvis.deployment.core.api.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Inherited
@Documented
@Deployment
@Target(ElementType.TYPE)
@Repeatable(Deploys.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deploy {

    @AliasFor(annotation = Deployment.class)
    String value() default "";

    @AliasFor(annotation = Deployment.class)
    String name() default "";

    @AliasFor(annotation = Deployment.class)
    int order() default 0;

    @AliasFor(annotation = Deployment.class)
    String[] env() default {};

    @AliasFor(annotation = Deployment.class)
    String delayed() default "";

    @AliasFor(annotation = Deployment.class)
    Scope scope() default Scope.DEFAULT;

}
