package com.github.markostijak.jarvis.deployment.core.support;

import static com.github.markostijak.jarvis.deployment.core.internal.utils.Utils.orElse;
import static com.github.markostijak.jarvis.deployment.core.internal.utils.Utils.orElseGet;
import static java.util.Objects.requireNonNull;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Scope;

import java.lang.annotation.Annotation;
import java.util.Arrays;

@SuppressWarnings("ClassExplicitlyAnnotation")
public class DeploymentBean implements Deployment {

    private final String name;
    private final Integer order;
    private final String delayed;
    private final Scope scope;

    public DeploymentBean(String name) {
        this(name, null, null, null);
    }

    public DeploymentBean(String name, String delayed) {
        this(name, null, null, delayed);
    }

    public DeploymentBean(String name, Scope scope) {
        this(name, scope, null, null);
    }

    public DeploymentBean(String name, Scope scope, String delayed) {
        this(name, scope, null, delayed);
    }

    public DeploymentBean(String name, Scope scope, Integer order, String delayed) {
        this.name = requireNonNull(name);
        this.delayed = orElseGet(delayed, () -> "0ms");
        this.scope = orElse(scope, Scope.JVM);
        this.order = orElse(order, 0);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return name;
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public String[] env() {
        return new String[0];
    }

    @Override
    public String delayed() {
        return delayed;
    }

    @Override
    public Scope scope() {
        return scope;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Deployment.class;
    }

    @Override
    public String toString() {
        return "DeploymentBean{" +
               "name=" + name() +
               ", order=" + order() +
               ", scope=" + scope() +
               ", delayed=" + delayed() +
               ", env=" + Arrays.toString(env()) +
               '}';
    }

}
