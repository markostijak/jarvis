package com.mscode.jarvis.engine.annotation;

import com.mscode.jarvis.engine.internal.JarvisConfiguration;
import com.mscode.jarvis.engine.internal.JarvisDelegatingListener;
import com.mscode.jarvis.engine.internal.JarvisTestContextBootstrapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.test.annotation.DirtiesContext.ClassMode;
import static org.springframework.test.annotation.DirtiesContext.HierarchyMode;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@OverrideAutoConfiguration(enabled = false)
@BootstrapWith(JarvisTestContextBootstrapper.class)
@TestExecutionListeners(listeners = JarvisDelegatingListener.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS, hierarchyMode = HierarchyMode.EXHAUSTIVE)
@ContextConfiguration(classes = JarvisConfiguration.class, initializers = ConfigDataApplicationContextInitializer.class)
public @interface JarvisTest {
}
