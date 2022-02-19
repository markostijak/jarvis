package com.mscode.jarvis.deployment.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import static org.springframework.test.context.TestContextAnnotationUtils.findMergedAnnotation;


@Slf4j
@Component
public class RedisExecutionListener implements TestExecutionListener, Ordered {

    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public RedisExecutionListener(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        DeployRedis deployRedis = findMergedAnnotation(testContext.getTestClass(), DeployRedis.class);
        if (deployRedis != null && deployRedis.flushAllBeforeTest()) {
            redisTemplate.execute((RedisCallback<?>) connection -> {
                log.info("Executing flushAll() on redis");
                connection.flushAll();
                return null;
            });
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
