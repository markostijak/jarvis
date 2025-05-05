package com.github.markostijak.jarvis.engine.api.annotations;

import com.github.markostijak.jarvis.engine.internal.JarvisComponentsImportSelector;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Inherited
@Documented
@Target(ElementType.TYPE)
@EnableConfigurationProperties
@Retention(RetentionPolicy.RUNTIME)
@Import(JarvisComponentsImportSelector.class)
public @interface ImportComponents {
}
