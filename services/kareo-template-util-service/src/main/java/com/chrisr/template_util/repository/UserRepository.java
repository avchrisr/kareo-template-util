package com.chrisr.template_util.repository;

import com.chrisr.template_util.exception.AppException;
import com.chrisr.template_util.exception.UserNotFoundException;
import com.chrisr.template_util.repository.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class UserRepository extends RepositoryBase {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public UserRepository(@Qualifier("postgresJdbcTemplate") NamedParameterJdbcTemplate postgresNamedParameterJdbcTemplate) {
        super(postgresNamedParameterJdbcTemplate);
    }

    public List<User> getUsers() {
        return postgresNamedParameterJdbcTemplate.query(GET_USERS_QUERY, new MapSqlParameterSource(), USER_ROW_MAPPER);
    }

    public User getUserById(long id) {
        String errorMessage = String.format("User not found with id = %s", id);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        try {
            PGobject pgObject = postgresNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_ID_QUERY, params, PGobject.class);
            if (pgObject == null) {
                throw new UserNotFoundException(errorMessage);
            }
            return objectMapper.readValue(pgObject.getValue(), User.class);
        } catch (EmptyResultDataAccessException ex) {
            throw new UserNotFoundException(errorMessage, ex);
        } catch (IOException ex) {
            throw new AppException(ex.getMessage(), ex);
        }
    }

    public User getUserByUsername(String username) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        // TODO: add index on username? no need. the number of users will only be a few

        try {
            PGobject pgObject = postgresNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_USERNAME_QUERY, params, PGobject.class);
            if (pgObject == null) {
                String errorMessage = String.format("User not found with username = %s", username);
                throw new UserNotFoundException(errorMessage);
            }
            return objectMapper.readValue(pgObject.getValue(), User.class);
        } catch (EmptyResultDataAccessException ex) {
            String errorMessage = String.format("User not found with username = %s", username);
            throw new UserNotFoundException(errorMessage, ex);
        } catch (IOException ex) {
            throw new AppException(ex.getMessage(), ex);
        }
    }

    public boolean existsByUsername(String username) {
        Integer result;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        try {
            result = postgresNamedParameterJdbcTemplate.queryForObject(USER_EXISTS_BY_USERNAME_QUERY, params, Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            result = null;
        }

        return result != null;
    }

    public void addUser(User user) {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("user", objectMapper.writeValueAsString(user));
            postgresNamedParameterJdbcTemplate.update(INSERT_USER_QUERY, params);
        } catch (JsonProcessingException e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    public void deleteUserById(long id) {
        // check if user with id exists
        getUserById(id);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        postgresNamedParameterJdbcTemplate.update(DELETE_USER_BY_ID_QUERY, params);
    }

    // ---------------------------
    // SQL QUERIES AND MAPPERS
    // ---------------------------

    private static final String GET_USERS_QUERY = "SELECT data FROM users";
    private static final String GET_USER_BY_ID_QUERY = "SELECT data FROM users WHERE (data->>'id')::bigint = :id";
    private static final String GET_USER_BY_USERNAME_QUERY = "SELECT data FROM users WHERE data->>'username' = :username";
    private static final String USER_EXISTS_BY_USERNAME_QUERY = "SELECT 1 FROM users WHERE data->>'username' = :username";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (data) VALUES (:user::jsonb)";
    private static final String DELETE_USER_BY_ID_QUERY = "DELETE FROM users WHERE (data->>'id')::bigint = :id";

    private final RowMapper<User> USER_ROW_MAPPER = (rs, i) -> {
        User user;

        try {
            user = objectMapper.readValue(rs.getString("data"), User.class);
        } catch (IOException ex) {
            throw new AppException(ex.getMessage(), ex);
        }

        return user;
    };
}
