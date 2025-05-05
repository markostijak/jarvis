package com.github.markostijak.jarvis.deployment.core.support;

import com.github.markostijak.jarvis.deployment.core.annotations.Deployment;
import com.github.markostijak.jarvis.deployment.core.api.Scope;
import com.github.markostijak.jarvis.deployment.core.api.Service;

import java.util.Objects;

public abstract class AbstractService implements Service {

    protected final Deployment deployment;

    public AbstractService(Deployment deployment) {
        this.deployment = deployment;
    }

    @Override
    public String getName() {
        return deployment.name();
    }

    @Override
    public int getOrder() {
        return deployment.order();
    }

    @Override
    public Scope getScope() {
        return deployment.scope();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "name=" + getName() +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractService that = (AbstractService) o;
        return Objects.equals(getName(), that.getName()) &&
               Objects.equals(getScope(), that.getScope());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getScope());
    }

}
