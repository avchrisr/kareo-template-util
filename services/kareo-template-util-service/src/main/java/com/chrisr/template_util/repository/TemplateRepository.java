package com.chrisr.template_util.repository;

import com.chrisr.template_util.exception.AppException;
import com.chrisr.template_util.repository.entity.template.Template;
import com.chrisr.template_util.repository.entity.template.TemplateCarePlan;
import com.chrisr.template_util.repository.entity.template.TemplateSection;
import com.chrisr.template_util.repository.entity.template.TemplateSpecialty;
import com.chrisr.template_util.exception.IllegalArgumentException;
import com.chrisr.template_util.exception.ResourceNotFoundException;
import com.chrisr.template_util.repository.enums.OracleTableName;
import com.chrisr.template_util.repository.enums.PostgresTableName;
import com.chrisr.template_util.repository.enums.TemplateType;
import com.chrisr.template_util.request.UpdateTemplateMetadataRequest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TemplateRepository {
    private static final Logger logger = LoggerFactory.getLogger(TemplateRepository.class);

    private final NamedParameterJdbcTemplate oracleNamedParameterJdbcTemplate;
    private final NamedParameterJdbcTemplate oracleQaNamedParameterJdbcTemplate;
    private NamedParameterJdbcTemplate oracleProdNamedParameterJdbcTemplate;
    private final NamedParameterJdbcTemplate postgresNamedParameterJdbcTemplate;

    @Autowired
    public TemplateRepository(@Qualifier("oracleJdbcTemplate") NamedParameterJdbcTemplate oracleNamedParameterJdbcTemplate,
                              @Qualifier("oracleJdbcTemplateQA") NamedParameterJdbcTemplate oracleQaNamedParameterJdbcTemplate,
                              @Qualifier("postgresJdbcTemplate") NamedParameterJdbcTemplate postgresNamedParameterJdbcTemplate) {
        this.oracleNamedParameterJdbcTemplate = oracleNamedParameterJdbcTemplate;
        this.oracleQaNamedParameterJdbcTemplate = oracleQaNamedParameterJdbcTemplate;
        this.postgresNamedParameterJdbcTemplate = postgresNamedParameterJdbcTemplate;
    }

    public Long getUserId(String environment, String username) {
        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);

        Long userId;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username.strip().toUpperCase());

        try {
            userId = oracleJdbcTemplate.queryForObject(GET_USER_ID_QUERY, params, Long.class);
        } catch (Exception e) {
            String errorMessage = String.format("No UserId found with username = %s", username);
            logger.info(errorMessage);
//            logger.info("No UserId found with username = {}", username);
            throw new ResourceNotFoundException(errorMessage);
        }

        return userId;
    }

    public List<Template> searchForTemplates(String environment, String templateId, String title, String findPartialTitleMatches, String type, String author, String version, String username) {

        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);
        MapSqlParameterSource params = new MapSqlParameterSource();

        String baseQuery = "SELECT t.TEMPLATES_ID, tt.NAME AS TYPE, t.TITLE, t.AUTHOR, t.VERSION, t.CREATE_DT, t.LAST_MOD_DT, u.USERNAME " +
                            "FROM HEALTHCARE.TEMPLATES t JOIN HEALTHCARE.TEMPLATE_TYPE tt ON tt.TEMPLATE_TYPE_ID = t.TEMPLATE_TYPE_ID " +
                            "LEFT JOIN REG.USERS u ON u.USERID = t.USER_ID " +
                            "WHERE t.IS_DELETED = 0 AND t.IS_PUBLISHED = 1";

        StringBuilder stringBuilder = new StringBuilder(baseQuery);

        if (templateId != null && !templateId.isBlank()) {
            params.addValue("templateId", templateId.strip());
            stringBuilder.append(" AND t.TEMPLATES_ID = :templateId");
        } else {
            if (title != null && !title.isBlank()) {
                if ("true".equalsIgnoreCase(findPartialTitleMatches)) {
                    params.addValue("title", "%" + title.strip().toUpperCase() + "%");
                } else {
                    params.addValue("title", title.strip().toUpperCase());
                }
                stringBuilder.append(" AND UPPER(TRIM(t.TITLE)) LIKE :title");
            }

            if (type != null && !type.isBlank()) {
                Integer templateTypeId = getTemplateTypeId(environment, TemplateType.valueOf(type.strip().toUpperCase()));
                params.addValue("templateTypeId", templateTypeId);
                stringBuilder.append(" AND t.TEMPLATE_TYPE_ID = :templateTypeId");
            }

            if (author != null && !author.isBlank()) {
                params.addValue("author", author);
                stringBuilder.append(" AND t.AUTHOR = :author");
            }

            if (version != null && !version.isBlank()) {
                params.addValue("version", version);
                stringBuilder.append(" AND t.VERSION = :version");
            }

            if (username != null && !username.isBlank()) {
                params.addValue("username", username);
                stringBuilder.append(" AND u.USERNAME = :username");

                // to validate that the username exists
                Long userId = getUserId(environment, username);

//            params.addValue("userId", userId);
//            stringBuilder.append(" AND USER_ID = :userId");
            }

            // TODO: implement pagination offset
            stringBuilder.append(" AND ROWNUM <= 100 ORDER BY t.TITLE ASC, t.TEMPLATES_ID DESC");
        }

        return oracleJdbcTemplate.query(stringBuilder.toString(), params, TEMPLATE_SEARCH_ROW_MAPPER);
    }

    public Integer getTemplateCount(String environment, String templateType, Long userId, long[] templateIdsFromRequest) {

//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("startDate", startPeriod.toDate(), Types.DATE);
//        params.addValue("endDate", endPeriod.toDate(), Types.DATE);
//        params.addValue("providerIds", providerIds);

        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);

        MapSqlParameterSource params = new MapSqlParameterSource();

        List<Long> templateIds = new ArrayList<>();
        for (long id : templateIdsFromRequest) {
            templateIds.add(id);
        }

        params.addValue("templateType", templateType.toUpperCase());
        params.addValue("templateIds", templateIds);

        String query =
                "SELECT COUNT(TEMPLATES_ID) AS MY_COUNT " +
                        "FROM HEALTHCARE.TEMPLATES t JOIN HEALTHCARE.TEMPLATE_TYPE tt ON t.TEMPLATE_TYPE_ID = tt.TEMPLATE_TYPE_ID " +
                        "WHERE UPPER(tt.NAME) = :templateType AND TEMPLATES_ID IN (:templateIds)";

        if (userId != null) {
            query += " AND USER_ID = :userId";
            params.addValue("userId", userId);
        }

        return oracleJdbcTemplate.queryForObject(query, params, Integer.class);
    }

    public List<Template> getTemplatesByIds(String environment, String templateType, Long userId, long[] templateIdsFromRequest) {

//        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//        namedParameters.addValue("startDate", startPeriod.toDate(), Types.DATE);
//        namedParameters.addValue("endDate", endPeriod.toDate(), Types.DATE);
//        namedParameters.addValue("providerIds", providerIds);

        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);

        String query;
        MapSqlParameterSource params = new MapSqlParameterSource();

        List<Long> templateIds = new ArrayList<>();
        for (long id : templateIdsFromRequest) {
            templateIds.add(id);
        }

        params.addValue("templateIds", templateIds);

        if (templateType != null) {
            params.addValue("templateType", templateType.toUpperCase());
            query = "SELECT TEMPLATES_ID, AUTHOR, VERSION, TITLE, CREATE_DT, LAST_MOD_DT " +
                    "FROM HEALTHCARE.TEMPLATES t JOIN HEALTHCARE.TEMPLATE_TYPE tt ON t.TEMPLATE_TYPE_ID = tt.TEMPLATE_TYPE_ID " +
                    "WHERE UPPER(tt.NAME) = :templateType AND TEMPLATES_ID IN (:templateIds)";
        } else {
            query = "SELECT TEMPLATES_ID, AUTHOR, VERSION, TITLE, CREATE_DT, LAST_MOD_DT " +
                    "FROM HEALTHCARE.TEMPLATES t WHERE TEMPLATES_ID IN (:templateIds)";
        }

        if (userId != null) {
            query += " AND USER_ID = :userId";
            params.addValue("userId", userId);
        }

        return oracleJdbcTemplate.query(query, params, TEMPLATE_ROW_MAPPER);
    }

    public List<TemplateSection> getTemplateSections(String environment, long templateId) {
        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);
        return oracleJdbcTemplate.query(GET_TEMPLATE_SECTIONS_QUERY, params, TEMPLATE_SECTION_ROW_MAPPER);
    }

    public List<TemplateCarePlan> getTemplateCarePlans(String environment, long templateId) {
        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);
        return oracleJdbcTemplate.query(GET_TEMPLATE_CAREPLANS_QUERY, params, TEMPLATE_CAREPLAN_ROW_MAPPER);
    }

    public List<TemplateSpecialty> getTemplateSpecialties(String environment, long templateId) {
        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);
        return oracleJdbcTemplate.query(GET_TEMPLATE_SPECIALTIES_QUERY, params, TEMPLATE_SPECIALTY_ROW_MAPPER);
    }

    public Integer getTemplateTypeId(String environment, TemplateType templateType) {
        switch (templateType) {
            case SYSTEM:
            case CUSTOM:
                break;
            default:
                String errorMessage = String.format("Unrecognized templateType Name: %s", templateType);
                throw new IllegalArgumentException(errorMessage);
        }

        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);

        String query = "SELECT TEMPLATE_TYPE_ID FROM HEALTHCARE.TEMPLATE_TYPE WHERE UPPER(NAME) = :templateType";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateType", templateType.name());

        return oracleJdbcTemplate.queryForObject(query, params, Integer.class);
    }

    public Long getNextSequence(String environment, OracleTableName oracleTableName) {
        String query;

        switch (oracleTableName) {
            case TEMPLATES:
                query = "SELECT HEALTHCARE.TEMPLATES_SEQ.NEXTVAL AS SEQ FROM DUAL";
                break;
            case TEMPLATE_SECTIONS:
                query = "SELECT HEALTHCARE.TEMPLATE_SECTIONS_SEQ.NEXTVAL AS SEQ FROM DUAL";
                break;
            case TEMPLATE_CAREPLANS:
                query = "SELECT HEALTHCARE.TEMPLATE_CAREPLANS_SEQ.NEXTVAL AS SEQ FROM DUAL";
                break;
            case TEMPLATE_SPECIALTIES:
                query = "SELECT HEALTHCARE.TEMPLATE_SPECIALTIES_SEQ.NEXTVAL AS SEQ FROM DUAL";
                break;
            default:
                String errorMessage = String.format("Unrecognized Database OracleTableName: %s", oracleTableName);
                throw new IllegalArgumentException(errorMessage);
        }

        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return oracleJdbcTemplate.queryForObject(query, params, Long.class);
    }

    public void insertTemplate(String environment, Long templateId, Template template, Long userId, Integer templateTypeId) {
        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);
        params.addValue("title", template.getTitle());
        params.addValue("author", template.getAuthor());
        params.addValue("version", template.getVersion());
        params.addValue("userId", userId);
        params.addValue("templateTypeId", templateTypeId);

        oracleJdbcTemplate.update(INSERT_TEMPLATE_QUERY, params);
    }

    public void insertTemplateSection(String environment, long templateId, TemplateSection templateSection) {
        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);

        long newTemplateSectionId = getNextSequence(environment, OracleTableName.TEMPLATE_SECTIONS);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", newTemplateSectionId);
        params.addValue("templateId", templateId);
        params.addValue("sectionMetaData", templateSection.getSectionMetaData());
        params.addValue("inherit", templateSection.getInherit());
        params.addValue("key", templateSection.getKey());

        oracleJdbcTemplate.update(INSERT_TEMPLATE_SECTION_QUERY, params);
    }

    public void insertTemplateCarePlan(String environment, long templateId, TemplateCarePlan templateCarePlan) {
        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);

        long newTemplateCarePlanId = getNextSequence(environment, OracleTableName.TEMPLATE_CAREPLANS);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", newTemplateCarePlanId);
        params.addValue("templateId", templateId);
        params.addValue("carePlanMetaData", templateCarePlan.getCarePlanMetaData());
        params.addValue("inherit", templateCarePlan.getInherit());
        params.addValue("key", templateCarePlan.getKey());

        oracleJdbcTemplate.update(INSERT_TEMPLATE_CAREPLAN_QUERY, params);
    }

    public void insertTemplateSpecialty(String environment, long templateId, TemplateSpecialty templateSpecialty) {
        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(environment);

        long newTemplateSpecialtyId = getNextSequence(environment, OracleTableName.TEMPLATE_SPECIALTIES);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", newTemplateSpecialtyId);
        params.addValue("templateId", templateId);
        params.addValue("description", templateSpecialty.getDescription());

        oracleJdbcTemplate.update(INSERT_TEMPLATE_SPECIALTY_QUERY, params);
    }

    public void updateTemplateMetadata(Template existingTemplate, UpdateTemplateMetadataRequest updateTemplateMetadataRequest) {

        NamedParameterJdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplateForEnvironment(updateTemplateMetadataRequest.getEnvironment());

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", updateTemplateMetadataRequest.getCurrentTemplateId());

        if (updateTemplateMetadataRequest.getNewTitle() != null && !updateTemplateMetadataRequest.getNewTitle().isBlank()) {
            params.addValue("title", updateTemplateMetadataRequest.getNewTitle());
        } else {
            params.addValue("title", existingTemplate.getTitle());
        }

        if (updateTemplateMetadataRequest.getNewAuthor() != null && !updateTemplateMetadataRequest.getNewAuthor().isBlank()) {
            params.addValue("author", updateTemplateMetadataRequest.getNewAuthor());
        } else {
            params.addValue("author", existingTemplate.getAuthor());
        }

        if (updateTemplateMetadataRequest.getNewVersion() != null && !updateTemplateMetadataRequest.getNewVersion().isBlank()) {
            params.addValue("version", updateTemplateMetadataRequest.getNewVersion());
        } else {
            params.addValue("version", existingTemplate.getVersion());
        }

        oracleJdbcTemplate.update(UPDATE_TEMPLATE_METADATA_QUERY, params);
    }

    public void deleteTemplate(long templateId) {
        // does soft-delete

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);

        oracleNamedParameterJdbcTemplate.update(DELETE_TEMPLATE_QUERY, params);
        oracleNamedParameterJdbcTemplate.update(DELETE_TEMPLATE_SPECIALTY_QUERY, params);
        oracleNamedParameterJdbcTemplate.update(DELETE_TEMPLATE_SECTION_QUERY, params);
        oracleNamedParameterJdbcTemplate.update(DELETE_TEMPLATE_CAREPLAN_QUERY, params);
    }

    public void replaceUserDeactivatedTemplateId(long oldTemplateId, long newTemplateId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("oldTemplateId", oldTemplateId);
        params.addValue("newTemplateId", newTemplateId);

        oracleNamedParameterJdbcTemplate.update(UPDATE_USER_DEACTIVATED_TEMPLATE_QUERY, params);
    }

    public void insertRequest(PostgresTableName postgresTableName, String request) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("request", request);

        // Not vulnerable to SQL Injection here because tableName comes from internally
        String query = "INSERT INTO " + postgresTableName.getName() + " (data) VALUES (:request::jsonb)";

        postgresNamedParameterJdbcTemplate.update(query, params);
    }

    public Long getNextPostgresSequence() {
        return postgresNamedParameterJdbcTemplate.queryForObject(GET_NEXT_POSTGRES_SEQUENCE, new MapSqlParameterSource(), Long.class);
    }

    private NamedParameterJdbcTemplate getOracleJdbcTemplateForEnvironment(String environment) {
        if ("dev".equalsIgnoreCase(environment)) {
            return oracleNamedParameterJdbcTemplate;
        } else if ("qa".equalsIgnoreCase(environment)) {
            return oracleQaNamedParameterJdbcTemplate;
        } else if ("prod".equalsIgnoreCase(environment)) {
            String jdbcUrl = System.getenv("ORACLE_PROD_DB_JDBC_URL");
            String username = System.getenv("ORACLE_PROD_DB_USERNAME");
            String password = System.getenv("ORACLE_PROD_DB_PASSWORD");

            if (jdbcUrl == null || jdbcUrl.isBlank() ||
                username == null || username.isBlank() ||
                password == null || password.isBlank()) {
                // default to QA
                logger.warn("Oracle PROD Jdbc Environment Variables Not Found. Defaulting to Oracle QA...");
                return oracleQaNamedParameterJdbcTemplate;
            }

            if (oracleProdNamedParameterJdbcTemplate != null) {
                return oracleProdNamedParameterJdbcTemplate;
            }

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(jdbcUrl);
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
//            hikariConfig.setMaximumPoolSize(10);
            DataSource dataSource = new HikariDataSource(hikariConfig);

            oracleProdNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            return oracleProdNamedParameterJdbcTemplate;
        } else {
            String errorMessage = String.format("Unrecognized environment = %s", environment);
            logger.error(errorMessage);
            throw new AppException(errorMessage);
        }
    }

    // ---------------------------
    // SQL QUERIES AND MAPPERS
    // ---------------------------

    private static final String GET_NEXT_POSTGRES_SEQUENCE = "SELECT nextval('sequence_number')";

    private static final String GET_USER_ID_QUERY = "SELECT USERID FROM REG.USERS WHERE UPPER(TRIM(USERNAME)) = :username";

    private final RowMapper<Template> TEMPLATE_SEARCH_ROW_MAPPER = (rs, i) -> {
        Template template = new Template();

        template.setId(rs.getLong("TEMPLATES_ID"));
        if ("System".equalsIgnoreCase(rs.getString("TYPE"))) {
            template.setType("System");
        } else {
            template.setType("Custom");
        }
        template.setAuthor(rs.getString("AUTHOR"));
        template.setVersion(rs.getString("VERSION"));
        template.setTitle(rs.getString("TITLE"));
        template.setCreatedOn(rs.getString("CREATE_DT").substring(0, 10));          // 2018-10-11 06:45:35
        template.setUpdatedOn(rs.getString("LAST_MOD_DT").substring(0, 10));
        template.setUsername(rs.getString("USERNAME"));

        return template;
    };

    private final RowMapper<Template> TEMPLATE_ROW_MAPPER = (rs, i) -> {
        Template template = new Template();

        template.setId(rs.getLong("TEMPLATES_ID"));
        template.setAuthor(rs.getString("AUTHOR"));
        template.setVersion(rs.getString("VERSION"));
        template.setTitle(rs.getString("TITLE"));
        template.setCreatedOn(rs.getString("CREATE_DT").substring(0, 10));          // 2018-10-11 06:45:35
        template.setUpdatedOn(rs.getString("LAST_MOD_DT").substring(0, 10));

        return template;
    };

    private static final String GET_TEMPLATE_SECTIONS_QUERY =
            "SELECT SECTION_METADATA, INHERIT, KEY FROM HEALTHCARE.TEMPLATE_SECTIONS WHERE TEMPLATES_ID = :templateId";

    private final RowMapper<TemplateSection> TEMPLATE_SECTION_ROW_MAPPER = (rs, i) -> {
        TemplateSection templateSection = new TemplateSection();

        templateSection.setInherit(rs.getString("INHERIT"));
        templateSection.setKey(rs.getString("KEY"));
        templateSection.setSectionMetaData(rs.getString("SECTION_METADATA"));       // verified that this worked without using Clob type
        return templateSection;
    };

    private static final String GET_TEMPLATE_CAREPLANS_QUERY =
            "SELECT CAREPLAN_METADATA, INHERIT, KEY FROM HEALTHCARE.TEMPLATE_CAREPLANS WHERE TEMPLATES_ID = :templateId";

    private final RowMapper<TemplateCarePlan> TEMPLATE_CAREPLAN_ROW_MAPPER = (rs, i) -> {
        TemplateCarePlan templateCarePlan = new TemplateCarePlan();

        templateCarePlan.setInherit(rs.getString("INHERIT"));
        templateCarePlan.setKey(rs.getString("KEY"));
        templateCarePlan.setCarePlanMetaData(rs.getString("CAREPLAN_METADATA"));
        return templateCarePlan;
    };

    private static final String GET_TEMPLATE_SPECIALTIES_QUERY =
            "SELECT DESCR FROM HEALTHCARE.TEMPLATE_SPECIALTIES WHERE TEMPLATES_ID = :templateId";

    private final RowMapper<TemplateSpecialty> TEMPLATE_SPECIALTY_ROW_MAPPER = (rs, i) -> {
        TemplateSpecialty templateSpecialty = new TemplateSpecialty();
        templateSpecialty.setDescription(rs.getString("DESCR"));
        return templateSpecialty;
    };

    private static final String UPDATE_TEMPLATE_METADATA_QUERY =
            "UPDATE HEALTHCARE.TEMPLATES SET TITLE = :title, AUTHOR = :author, VERSION = :version, LAST_MOD_DT = SYSDATE WHERE TEMPLATES_ID = :templateId";

    private static final String INSERT_TEMPLATE_QUERY =
            "INSERT INTO HEALTHCARE.TEMPLATES (TEMPLATES_ID, TITLE, AUTHOR, VERSION, CREATE_DT, LAST_MOD_DT, IS_PUBLISHED, IS_DELETED, USER_ID, TEMPLATE_TYPE_ID) VALUES " +
            "(:templateId, :title, :author, :version, SYSDATE, SYSDATE, 1, 0, :userId, :templateTypeId)";

    private static final String INSERT_TEMPLATE_SPECIALTY_QUERY =
            "INSERT INTO HEALTHCARE.TEMPLATE_SPECIALTIES (TEMPLATE_SPECIALTIES_ID, TEMPLATES_ID, DESCR, CREATE_DT, LAST_MOD_DT, IS_DELETED) VALUES " +
                    "(:id, :templateId, :description, SYSDATE, SYSDATE, 0)";

    private static final String INSERT_TEMPLATE_SECTION_QUERY =
            "INSERT INTO HEALTHCARE.TEMPLATE_SECTIONS (TEMPLATE_SECTIONS_ID, TEMPLATES_ID, SECTION_METADATA, INHERIT, KEY, CREATE_DT, LAST_MOD_DT, IS_DELETED) VALUES " +
                    "(:id, :templateId, :sectionMetaData, :inherit, :key, SYSDATE, SYSDATE, 0)";

    private static final String INSERT_TEMPLATE_CAREPLAN_QUERY =
            "INSERT INTO HEALTHCARE.TEMPLATE_CAREPLANS (TEMPLATE_CAREPLANS_ID, TEMPLATES_ID, CAREPLAN_METADATA, INHERIT, KEY, CREATE_DT, LAST_MOD_DT, IS_DELETED) VALUES " +
                    "(:id, :templateId, :carePlanMetaData, :inherit, :key, SYSDATE, SYSDATE, 0)";

    private static final String DELETE_TEMPLATE_QUERY = "UPDATE HEALTHCARE.TEMPLATES SET LAST_MOD_DT = SYSDATE, IS_DELETED = 1 WHERE TEMPLATES_ID = :templateId";
    private static final String DELETE_TEMPLATE_SPECIALTY_QUERY = "UPDATE HEALTHCARE.TEMPLATE_SPECIALTIES SET LAST_MOD_DT = SYSDATE, IS_DELETED = 1 WHERE TEMPLATES_ID = :templateId";
    private static final String DELETE_TEMPLATE_SECTION_QUERY = "UPDATE HEALTHCARE.TEMPLATE_SECTIONS SET LAST_MOD_DT = SYSDATE, IS_DELETED = 1 WHERE TEMPLATES_ID = :templateId";
    private static final String DELETE_TEMPLATE_CAREPLAN_QUERY = "UPDATE HEALTHCARE.TEMPLATE_CAREPLANS SET LAST_MOD_DT = SYSDATE, IS_DELETED = 1 WHERE TEMPLATES_ID = :templateId";

    private static final String UPDATE_USER_DEACTIVATED_TEMPLATE_QUERY = "UPDATE HEALTHCARE.USER_DEACTIVATED_TEMPLATES SET TEMPLATES_ID = :newTemplateId WHERE TEMPLATES_ID = :oldTemplateId";
}
