package com.github.markostijak.jarvis.services.postgres;

import java.sql.Driver;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.util.ClassUtils;

@Data
public class PostgresConnectionDetails implements JdbcConnectionDetails {

    private String username = "test";

    private String password = "test";

    private String url = "jdbc:postgresql://localhost:5432/test-db";

    @SuppressWarnings("unchecked")
    public Class<Driver> getDriverClass() throws ClassNotFoundException {
        return (Class<Driver>) ClassUtils.forName(getDriverClassName(), ClassUtils.getDefaultClassLoader());
    }

    @Override
    public String getJdbcUrl() {
        return url;
    }

}
