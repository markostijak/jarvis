package com.mscode.jarvis.example;

import com.mscode.jarvis.deployment.kafka.DeployKafka;
import com.mscode.jarvis.deployment.kafka.repository.EnableKafkaRepository;
import com.mscode.jarvis.deployment.kafka.repository.KafkaRepository;
import com.mscode.jarvis.deployment.mysql.DeployMySql;
import com.mscode.jarvis.deployment.redis.DeployRedis;
import com.mscode.jarvis.engine.annotation.Deploy;
import com.mscode.jarvis.engine.annotation.JarvisTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

@JarvisTest
@Deploy(name = "mongo", order = 1)
@DeployMySql(order = 1)
@DeployKafka(order = 1, delayed = 20)
@DeployRedis(order = 1, flushAllBeforeTest = true)
@EnableKafkaRepository(topics = "test")
public class ExampleTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private KafkaRepository kafkaRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test() {
        redisTemplate.opsForValue().set("test", "test");

        String test = kafkaRepository.getEvent("test");
        System.out.println(test);

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("show tables");
        sqlRowSet.next();

        final String test1 = redisTemplate.opsForValue().get("test");
        System.out.println(test1);
    }

}
