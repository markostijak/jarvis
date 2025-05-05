package com.github.markostijak.jarvis.deployment.core.annotations;

import com.github.markostijak.jarvis.deployment.core.api.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AliasFor;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Deployment {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    int order() default 0;

    String[] env() default {};

    String delayed() default "0ms";

    @Value("jarvis.deployment.scope")
    Scope scope() default Scope.CLASS;

}
