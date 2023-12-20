package com.github.markostijak.jarvis.services.postgres;

import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentProperties;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentProperties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@ComponentScan
@AutoConfiguration
public class PostgresAutoConfiguration {

    @Bean
    @ConditionalOnClass(DockerDeploymentProperties.class)
    public PostgresConnectionDetails dockerPostgresConnectionDetails(Environment environment) {
        String key = "jarvis.deployment.docker.services.postgres.connection-details";
        return Binder.get(environment).bindOrCreate(key, PostgresConnectionDetails.class);
    }

    @Bean
    @ConditionalOnClass(KubernetesDeploymentProperties.class)
    public PostgresConnectionDetails kubernetesPostgresConnectionDetails(Environment environment) {
        String key = "jarvis.deployment.kubernetes.services.postgres.connection-details";
        return Binder.get(environment).bindOrCreate(key, PostgresConnectionDetails.class);
    }

    @Bean
    @ConditionalOnClass(JdbcTemplate.class)
    public PostgresTemplate postgresTemplate(@Autowired(required = false) DataSource dataSource, PostgresConnectionDetails connectionDetails) throws Exception {
        if (dataSource != null) {
            return new PostgresTemplate(new JdbcTemplate(dataSource));
        }

        SimpleDriverDataSource simpleDriverDataSource = new SimpleDriverDataSource();
        simpleDriverDataSource.setDriverClass(connectionDetails.getDriverClass());
        simpleDriverDataSource.setUsername(connectionDetails.getUsername());
        simpleDriverDataSource.setPassword(connectionDetails.getPassword());
        simpleDriverDataSource.setUrl(connectionDetails.getJdbcUrl());

        return new PostgresTemplate(new JdbcTemplate(simpleDriverDataSource));
    }

}
