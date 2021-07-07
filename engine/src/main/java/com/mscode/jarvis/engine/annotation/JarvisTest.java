package com.mscode.jarvis.engine.annotation;

import com.mscode.jarvis.engine.internal.JarvisConfiguration;
import com.mscode.jarvis.engine.internal.JarvisDelegatingListener;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@OverrideAutoConfiguration(enabled = false)
@BootstrapWith(DefaultTestContextBootstrapper.class)
@TestExecutionListeners(listeners = JarvisDelegatingListener.class, mergeMode = MERGE_WITH_DEFAULTS)
@ContextConfiguration(classes = JarvisConfiguration.class, initializers = ConfigDataApplicationContextInitializer.class)
public @interface JarvisTest {
}
