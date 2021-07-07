package com.mscode.jarvis.deployment.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@Component
public class RedisExecutionListener implements TestExecutionListener {

    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public RedisExecutionListener(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        DeployRedis deployRedis = findAnnotation(testContext.getTestClass(), DeployRedis.class);
        if (deployRedis != null && deployRedis.flushAllBeforeTest()) {
            // flush all
        }
    }

}
