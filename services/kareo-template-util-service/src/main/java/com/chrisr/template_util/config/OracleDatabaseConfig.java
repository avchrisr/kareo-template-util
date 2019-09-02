package com.chrisr.template_util.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class OracleDatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean(name = "oracleDataSource")
    public DataSource dataSource() {

        /*
            DatabaseInfo databaseInfo = serviceRegistryClient.getDatabaseInfoByName("EhrDb");

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setMinimumIdle(2);
           // hikariConfig.setPoolName("incentivereportsOraclePool");
            hikariConfig.addDataSourceProperty("driverType", "thin");
            hikariConfig.setJdbcUrl(databaseInfo.getUrl());
            hikariConfig.setUsername(databaseInfo.getUserName());
            hikariConfig.setPassword(databaseInfo.getPassword());

            return new HikariDataSource(hikariConfig);
         */

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
//        hikariConfig.setMaximumPoolSize(10);            // TODO: learn how to configure and use Hikari pool size
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "oracleJdbcTemplate")
    public NamedParameterJdbcTemplate oracleJdbcTemplate(@Autowired @Qualifier("oracleDataSource") DataSource datasource) {
        return new NamedParameterJdbcTemplate(datasource);
    }
}

