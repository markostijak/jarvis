package com.github.markostijak.jarvis.engine.internal;


import com.github.markostijak.jarvis.engine.api.annotations.ImportComponents;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.context.annotation.DeterminableImports;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

public class JarvisComponentsImportSelector extends AutoConfigurationImportSelector implements DeterminableImports {

    @Override
    public Set<Object> determineImports(AnnotationMetadata metadata) {
        return Set.copyOf(getCandidateConfigurations(metadata, null));
    }

    @Override
    protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        Class<?> source = ClassUtils.resolveClassName(metadata.getClassName(), getBeanClassLoader());

        if (JarvisTestConfiguration.class.equals(source)) {
            return ImportCandidates.load(ImportComponents.class, getBeanClassLoader()).getCandidates();
        }

        return Collections.emptyList();
    }

    @Override
    protected AnnotationAttributes getAttributes(AnnotationMetadata metadata) {
        return null;
    }

    @Override
    protected Set<String> getExclusions(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        return Collections.emptySet();
    }

    @Override
    public int getOrder() {
        return super.getOrder() - 2;
    }

    @Override
    protected void handleInvalidExcludes(List<String> invalidExcludes) {
        // Ignore for test
    }

}
