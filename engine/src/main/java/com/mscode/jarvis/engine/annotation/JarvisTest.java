package com.mscode.jarvis.engine.annotation;

import com.mscode.jarvis.engine.internal.JarvisConfiguration;
import com.mscode.jarvis.engine.internal.JarvisDelegatingListener;
import com.mscode.jarvis.engine.internal.JarvisTestContextBootstrapper;
import com.mscode.jarvis.engine.internal.JarvisTestContextBootstrapper.JarvisContextLoader;
import com.mscode.jarvis.engine.test.JarvisTestConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@BootstrapWith(JarvisTestContextBootstrapper.class)
@TestExecutionListeners(listeners = JarvisDelegatingListener.class)
@ContextHierarchy({
        @ContextConfiguration(classes = JarvisConfiguration.class, loader = JarvisContextLoader.class),
        @ContextConfiguration(classes = JarvisTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
})
public @interface JarvisTest {
}
