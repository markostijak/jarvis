package com.github.markostijak.jarvis.services.postgres;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class PostgresTemplate extends NamedParameterJdbcTemplate {

    public PostgresTemplate(JdbcOperations classicJdbcTemplate) {
        super(classicJdbcTemplate);
    }

}
