package com.mscode.jarvis.deployment.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import static org.springframework.core.annotation.MergedAnnotations.from;

@Component
public class MySqlExecutionListener implements TestExecutionListener {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MySqlExecutionListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        MergedAnnotation<DeployMySql> deployMySql = from(testContext.getTestClass()).get(DeployMySql.class);
        // create snapshot
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        MergedAnnotation<DeployMySql> deployMySql = from(testContext.getTestClass()).get(DeployMySql.class);
        // restore to snapshot
    }

}
