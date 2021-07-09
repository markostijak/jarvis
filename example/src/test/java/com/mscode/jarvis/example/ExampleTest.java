package com.mscode.jarvis.example;

import com.mscode.jarvis.deployment.kafka.DeployKafka;
import com.mscode.jarvis.deployment.mysql.DeployMySql;
import com.mscode.jarvis.deployment.redis.DeployRedis;
import com.mscode.jarvis.engine.annotation.JarvisTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;

@JarvisTest
@DeployRedis(order = 1)
@DeployKafka(order = 2, delayed = 20)
@DeployMySql(order = 3)
public class ExampleTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<?, ?> kafkaTemplate;

    @Test
    public void test() {
        System.out.println("test");
    }

}
