package com.mscode.jarvis.engine.test;

import org.springframework.lang.NonNull;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractDirtiesContextTestExecutionListener;

public class JarvisDirtiesContextTestExecutionListener extends AbstractDirtiesContextTestExecutionListener {

    @Override
    public int getOrder() {
        return 3000;
    }

    @Override
    public void afterTestClass(@NonNull TestContext testContext) {
        dirtyContext(testContext, DirtiesContext.HierarchyMode.CURRENT_LEVEL);
    }

}
