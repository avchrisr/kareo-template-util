package com.chrisr.template_util.repository;

import com.chrisr.template_util.repository.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public abstract class RepositoryBase {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryBase.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    final NamedParameterJdbcTemplate postgresNamedParameterJdbcTemplate;

    public RepositoryBase(NamedParameterJdbcTemplate postgresNamedParameterJdbcTemplate) {
        this.postgresNamedParameterJdbcTemplate = postgresNamedParameterJdbcTemplate;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            users = postgresNamedParameterJdbcTemplate.query(GET_USERS_QUERY, params, USER_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }
        return users;
    }

    public Long getNextPostgresSequence() {
        return postgresNamedParameterJdbcTemplate.queryForObject(GET_NEXT_POSTGRES_SEQUENCE, new MapSqlParameterSource(), Long.class);
    }

    // ---------------------------
    // SQL QUERIES AND MAPPERS
    // ---------------------------
    private static final String GET_NEXT_POSTGRES_SEQUENCE = "SELECT nextval('sequence_number')";
    private static final String GET_USERS_QUERY = "SELECT data FROM users";

    private final RowMapper<User> USER_ROW_MAPPER = (rs, i) -> {
        User user = null;

        try {
            user = objectMapper.readValue(rs.getString("data"), User.class);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return user;
    };
}
