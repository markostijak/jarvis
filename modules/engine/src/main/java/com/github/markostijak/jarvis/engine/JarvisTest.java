package com.github.markostijak.jarvis.engine;

import com.github.markostijak.jarvis.engine.api.annotations.ImportComponents;
import com.github.markostijak.jarvis.engine.internal.JarvisTestContextBootstrapper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.Environment;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Documented
@ImportComponents
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@BootstrapWith(JarvisTestContextBootstrapper.class)
public @interface JarvisTest {

    /**
     * Alias for {@link #properties()}.
     *
     * @return the properties to apply
     */
    @AliasFor("properties")
    String[] value() default {};

    /**
     * Properties in form {@literal key=value} that should be added to the Spring
     * {@link Environment} before the test runs.
     *
     * @return the properties to add
     */
    @AliasFor("value")
    String[] properties() default {};

}
