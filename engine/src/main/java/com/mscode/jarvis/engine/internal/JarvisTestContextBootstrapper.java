package com.mscode.jarvis.engine.internal;

import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;

public class JarvisTestContextBootstrapper extends DefaultTestContextBootstrapper {

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
    }

}
