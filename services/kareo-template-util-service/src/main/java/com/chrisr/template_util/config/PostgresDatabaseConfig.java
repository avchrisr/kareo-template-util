package com.chrisr.template_util.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class PostgresDatabaseConfig {

    @Value("${postgres.datasource.url}")
    private String jdbcUrl;

    @Value("${postgres.datasource.username}")
    private String username;

    @Value("${postgres.datasource.password}")
    private String password;

    private static final String CREATE_SEQUENCE_QUERY = "CREATE SEQUENCE IF NOT EXISTS sequence_number MINVALUE 1";
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS $tableName ( data jsonb )";
    private static final String CREATE_INDEX_QUERY = "CREATE UNIQUE INDEX IF NOT EXISTS $tableName_primary_key ON $tableName USING btree (((data->>'id')::bigint))";
    private static final String[] TABLE_NAMES = {"users", "request_history", "audit_log"};

    @Bean(name = "postgresDataSource")
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
        hikariConfig.setMaximumPoolSize(5);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "postgresJdbcTemplate")
    public NamedParameterJdbcTemplate postgresJdbcTemplate(@Autowired @Qualifier("postgresDataSource") DataSource datasource) {

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(datasource);
        for (String tableName : TABLE_NAMES) {
            String tableQuery = CREATE_TABLE_QUERY.replace("$tableName", tableName);
            String indexQuery = CREATE_INDEX_QUERY.replace("$tableName", tableName);
            namedParameterJdbcTemplate.update(CREATE_SEQUENCE_QUERY, new MapSqlParameterSource());
            namedParameterJdbcTemplate.update(tableQuery, new MapSqlParameterSource());
            namedParameterJdbcTemplate.update(indexQuery, new MapSqlParameterSource());
        }

//        String insertUserQuery = "INSERT INTO users (data) VALUES (('{\"id\": 1, \"username\": \"admin\", \"password\": \"abc12345\", \"firstName\": \"House\", \"lastName\": \"Doctor\", \"dob\": \"2001-05-05\", \"email\": \"drhouse@clinic.com\"}')::jsonb)";
//        namedParameterJdbcTemplate.update(insertUserQuery, new MapSqlParameterSource());

        return namedParameterJdbcTemplate;
    }
}
