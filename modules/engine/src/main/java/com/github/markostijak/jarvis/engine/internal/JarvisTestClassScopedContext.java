package com.github.markostijak.jarvis.engine.internal;

import com.github.markostijak.jarvis.engine.api.JarvisContext;
import com.github.markostijak.jarvis.engine.api.JarvisTestContext;
import com.github.markostijak.jarvis.engine.support.attributes.ConcurrentAttributeAccessor;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class JarvisTestClassScopedContext extends ConcurrentAttributeAccessor implements JarvisTestContext {

    private final Class<?> testClass;
    private final JarvisContext jarvisContext;

    @NonNull
    @Override
    public Class<?> getTestClass() {
        return testClass;
    }

    @NonNull
    @Override
    public JarvisContext getParent() {
        return jarvisContext;
    }

    public void removeAllAttributes() {
        this.attributes.clear();
    }

}
