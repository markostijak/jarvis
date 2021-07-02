package com.mscode.jarvis.services.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

@Component
public class MySqlExecutionListener implements TestExecutionListener {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MySqlExecutionListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        // create snapshot
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        // restore to snapshot
    }

}
