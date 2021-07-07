package com.mscode.jarvis.example;

import com.mscode.jarvis.deployment.kafka.DeployKafka;
import com.mscode.jarvis.deployment.mysql.DeployMySql;
import com.mscode.jarvis.deployment.redis.DeployRedis;
import com.mscode.jarvis.engine.annotation.JarvisTest;
import org.junit.jupiter.api.Test;

@JarvisTest
@DeployMySql
@DeployRedis
@DeployKafka
public class ExampleTest {

    @Test
    public void test() {
        System.out.println("test");
    }

}
