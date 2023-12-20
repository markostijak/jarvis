package com.github.markostijak.jarvis.testing;

import com.github.markostijak.jarvis.engine.JarvisTest;
import com.github.markostijak.jarvis.services.wiremock.DeployWireMock;
import com.github.markostijak.jarvis.services.wiremock.WireMockClient;
import com.github.markostijak.jarvis.testing.components.TestConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JarvisTest
@DeployWireMock(delayed = "1s")
public class JarvisEngineTest {

    @Autowired
    private WireMockClient wiremock;

    @Autowired
    private TestConfiguration.TestBean testBean;

    @Test
    void test() {
        System.out.println(wiremock.baseUrl());
    }

}
