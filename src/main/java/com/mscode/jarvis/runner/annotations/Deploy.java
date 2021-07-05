package com.mscode.jarvis.runner.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Deployment
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deploy {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    int order() default 0;

    String[] env() default {};

    int delay() default -1;

}
