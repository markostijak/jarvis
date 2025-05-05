package com.github.markostijak.jarvis.testing;

import com.github.markostijak.jarvis.services.postgres.DeployPostgres;
import com.github.markostijak.jarvis.testing.components.TestConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DeployPostgres
public class SpringBootIntegrationTest {

    @Autowired
    private TestConfiguration.TestBean testBean;

    @Test
    void test() {
        System.out.println("test");
    }

}
