package com.mscode.jarvis;

import com.mscode.jarvis.runner.annotations.JarvisTest;
import com.mscode.jarvis.services.mysql.DeployMySql;
import com.mscode.jarvis.services.redis.DeployRedis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

@JarvisTest
@DeployMySql(order = 1, env = "test:test")
@DeployRedis(order = 2)
class ApplicationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test() {
        System.out.println("test");
    }

}
