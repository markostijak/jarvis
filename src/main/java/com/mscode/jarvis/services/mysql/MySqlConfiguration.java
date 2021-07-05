package com.mscode.jarvis.services.mysql;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ComponentScan
@Configuration(proxyBeanMethods = false)
public class MySqlConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "jarvis.services.mysql.connection")
    public DataSourceProperties mysqlProperties() {
        return new DataSourceProperties();
    }

}
