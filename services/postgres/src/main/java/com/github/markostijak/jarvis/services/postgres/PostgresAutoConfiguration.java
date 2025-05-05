package com.github.markostijak.jarvis.services.postgres;

import com.github.markostijak.jarvis.deployment.core.internal.utils.BinderUtils;
import com.github.markostijak.jarvis.deployment.docker.DockerDeploymentProperties;
import com.github.markostijak.jarvis.deployment.kubernetes.KubernetesDeploymentProperties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@ComponentScan
@AutoConfiguration
public class PostgresAutoConfiguration {

    @Bean
    @ConditionalOnClass(DockerDeploymentProperties.class)
    public PostgresConnectionDetails dockerPostgresConnectionDetails(Environment environment) {
        return BinderUtils.bind(environment, "docker", "postgres", PostgresConnectionDetails.class);
    }

    @Bean
    @ConditionalOnClass(KubernetesDeploymentProperties.class)
    public PostgresConnectionDetails kubernetesPostgresConnectionDetails(Environment environment) {
        return BinderUtils.bind(environment, "kubernetes", "postgres", PostgresConnectionDetails.class);
    }

    @Bean
    @Lazy
    @ConditionalOnClass(JdbcTemplate.class)
    public JdbcTemplate postgresTemplate(@Autowired(required = false) DataSource dataSource, PostgresConnectionDetails connectionDetails) throws Exception {
        if (dataSource != null) {
            return new JdbcTemplate(dataSource);
        }

        SimpleDriverDataSource simpleDriverDataSource = new SimpleDriverDataSource();
        simpleDriverDataSource.setDriverClass(connectionDetails.getDriverClass());
        simpleDriverDataSource.setUsername(connectionDetails.getUsername());
        simpleDriverDataSource.setPassword(connectionDetails.getPassword());
        simpleDriverDataSource.setUrl(connectionDetails.getJdbcUrl());

        return new JdbcTemplate(simpleDriverDataSource);
    }

}
