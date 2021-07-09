package com.mscode.jarvis.engine.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deployment {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    int order() default 0;

    String[] env() default {};

    int delayed() default 0;

}
