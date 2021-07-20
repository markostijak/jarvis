package com.mscode.jarvis.engine.internal;

import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;

import java.util.Collections;
import java.util.List;

public class JarvisTestContextBootstrapper extends DefaultTestContextBootstrapper {

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
    }

    private int disableImportsForRootApplicationContext = 0;

    @NonNull
    @Override
    protected List<ContextCustomizerFactory> getContextCustomizerFactories() {
        if (disableImportsForRootApplicationContext++ == 0) {
            return Collections.emptyList();
        }

        return super.getContextCustomizerFactories();
    }

    @NonNull
    @Override
    protected Class<? extends ContextLoader> getDefaultContextLoaderClass(@NonNull Class<?> testClass) {
        throw new UnsupportedOperationException("Use @ContextConfiguration annotation to define ContextLoader!");
    }

    public static class JarvisContextLoader extends AnnotationConfigContextLoader {

        @Override
        protected void prepareContext(@NonNull GenericApplicationContext applicationContext) {
            new ConfigDataApplicationContextInitializer().initialize(applicationContext);
        }

    }

}
