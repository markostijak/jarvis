package com.mscode.jarvis.deployment.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import static org.springframework.test.context.TestContextAnnotationUtils.findMergedAnnotation;

@Component
public class MySqlExecutionListener implements TestExecutionListener, Ordered {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MySqlExecutionListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        DeployMySql deployMySql = findMergedAnnotation(testContext.getTestClass(), DeployMySql.class);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        DeployMySql deployMySql = findMergedAnnotation(testContext.getTestClass(), DeployMySql.class);
        // create snapshot
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        DeployMySql deployMySql = findMergedAnnotation(testContext.getTestClass(), DeployMySql.class);
        // restore to snapshot
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {

    }

    @Override
    public int getOrder() {
        return 0;
    }

}
