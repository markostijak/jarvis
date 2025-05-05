package com.github.markostijak.jarvis.services.wiremock;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

@Component
@RequiredArgsConstructor
public class WireMockListener implements TestExecutionListener, Ordered {

    public static final String KEY = WireMockListener.class.getName();

    private final WireMockClient client;

    @Override
    public void beforeTestClass(TestContext testContext) {
        Stub stub = AnnotationUtils.getAnnotation(testContext.getTestClass(), Stub.class);
        registerStub(testContext, stub, KEY + "." + testContext.getTestClass().getName());
    }

    @Override
    public void beforeTestMethod(@NonNull TestContext testContext) {
        Stub stub = AnnotationUtils.getAnnotation(testContext.getTestMethod(), Stub.class);
        registerStub(testContext, stub, KEY + "." + testContext.getTestMethod().getName());
    }

    @Override
    public void afterTestMethod(@NonNull TestContext testContext) {
        removeStub(testContext, KEY + "." + testContext.getTestMethod().getName());
    }

    @Override
    public void afterTestClass(@NonNull TestContext testContext) {
        removeStub(testContext, KEY + "." + testContext.getTestClass().getName());
    }

    private void registerStub(TestContext testContext, Stub stub, String key) {
        if (stub == null) {
            return;
        }

        String json = stub.json();

        if (!StringUtils.hasText(json)) {
            String yaml = stub.yaml();

            if (!StringUtils.hasText(yaml)) {
                return;
            }

            json = Json.write(new Yaml().load(yaml));
        }

        StubMapping mapping = client.jsonStubFor(json);
        testContext.setAttribute(key, mapping);
    }

    private void removeStub(TestContext testContext, String key) {
        StubMapping mapping = (StubMapping) testContext.removeAttribute(key);

        if (mapping != null) {
            client.removeStubMapping(mapping);
        }
    }

    @Override
    public int getOrder() {
        return 101;
    }

}
