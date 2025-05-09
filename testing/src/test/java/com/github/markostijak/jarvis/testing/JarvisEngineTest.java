package com.github.markostijak.jarvis.testing;

import com.github.markostijak.jarvis.engine.JarvisTest;
import com.github.markostijak.jarvis.services.kafka.DeployKafka;
import com.github.markostijak.jarvis.services.postgres.DeployPostgres;
import com.github.markostijak.jarvis.services.redis.DeployRedis;
import com.github.markostijak.jarvis.services.wiremock.DeployWireMock;
import com.github.markostijak.jarvis.services.wiremock.WireMockClient;
import com.github.markostijak.jarvis.testing.components.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JarvisTest
@DeployRedis
@DeployKafka
@DeployPostgres
@DeployWireMock
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
