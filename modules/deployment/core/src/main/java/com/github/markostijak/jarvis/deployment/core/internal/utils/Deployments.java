package com.github.markostijak.jarvis.deployment.core.internal.utils;

import static com.github.markostijak.jarvis.deployment.core.internal.listeners.DeploymentListener.JARVIS_DEPLOYMENT_SCOPE;
import static java.util.stream.Collectors.toMap;
import static org.springframework.core.annotation.MergedAnnotations.SearchStrategy.TYPE_HIERARCHY;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Scope;
import com.github.markostijak.jarvis.deployment.core.internal.listeners.LoggingListener;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentDescriptor;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@UtilityClass
@SuppressWarnings("all")
public class Deployments {

    public static List<Deployment> list(Class<?> testClass, Environment environment) {
        return (List) MergedAnnotations.search(TYPE_HIERARCHY).from(testClass).stream(Deployment.class)
                .map(annotation -> new InheritanceAndEnvironmentAwareDeployment(environment, annotation))
                .toList();
    }

    public static Map<String, String> mergeEnvs(DeploymentDescriptor descriptor, Deployment deployment) {
        Map<String, String> parsed = Stream.of(deployment.env())
                .map(e -> e.split("=", -1))
                .collect(toMap(p -> p[0], p -> p[1]));

        return Utils.merge(descriptor.getEnv(), parsed);
    }

    public static Path resolveDirectoryFor(JarvisTestContext testContext, Deployment deployment) {
        Path rootDirectory = testContext.getParent().requireAttribute(LoggingListener.LOG_DIRECTORY);
        Map<Scope, Path> directories = testContext.requireAttribute(LoggingListener.LOG_DIRECTORY);
        return directories.getOrDefault(deployment.scope(), rootDirectory);
    }

    @RequiredArgsConstructor
    private static class InheritanceAndEnvironmentAwareDeployment implements Deployment {

        private final Map<String, Object> memoizer = new HashMap<>(10);

        private final Environment environment;
        private final MergedAnnotation<Deployment> delegate;

        @Override
        public String value() {
            return (String) memoizer.computeIfAbsent("value", delegate::getString);
        }

        @Override
        public String name() {
            return (String) memoizer.computeIfAbsent("name", delegate::getString);
        }

        @Override
        public int order() {
            return (int) memoizer.computeIfAbsent("order", delegate::getInt);
        }

        @Override
        public String[] env() {
            return (String[]) memoizer.computeIfAbsent("env", delegate::getStringArray);
        }

        @Override
        public String delayed() {
            return (String) memoizer.computeIfAbsent("delayed", d -> {
                String delayed = delegate.getString(d);

                if (StringUtils.hasText(delayed)) {
                    return delayed;
                }

                return ReflectionUtils.findMethod(Deployment.class, d).getDefaultValue();
            });
        }

        @Override
        public Scope scope() {
            return (Scope) memoizer.computeIfAbsent("scope", s -> {
                Scope scope = delegate.getEnum(s, Scope.class);

                if (scope != Scope.DEFAULT) {
                    return scope;
                }

                scope = environment.getProperty(JARVIS_DEPLOYMENT_SCOPE, Scope.class);

                if (scope != null && scope != Scope.DEFAULT) {
                    return scope;
                }

                return ReflectionUtils.findMethod(Deployment.class, s).getDefaultValue();
            });
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Deployment.class;
        }

        @Override
        public String toString() {
            return "Deployment{" +
                   "name=" + name() +
                   ", order=" + order() +
                   ", scope=" + scope() +
                   ", delayed=" + delayed() +
                   ", env=" + Arrays.toString(env()) +
                   '}';
        }
    }

}
