package com.chrisr.template_util.repository;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TemplateRepository {
    private static final Logger logger = LoggerFactory.getLogger(TemplateRepository.class);

    private final NamedParameterJdbcTemplate oracleNamedParameterJdbcTemplate;
    private final NamedParameterJdbcTemplate postgresNamedParameterJdbcTemplate;

    @Autowired
    public TemplateRepository(@Qualifier("oracleJdbcTemplate") NamedParameterJdbcTemplate oracleNamedParameterJdbcTemplate,
                              @Qualifier("postgresJdbcTemplate") NamedParameterJdbcTemplate postgresNamedParameterJdbcTemplate) {
        this.oracleNamedParameterJdbcTemplate = oracleNamedParameterJdbcTemplate;
        this.postgresNamedParameterJdbcTemplate = postgresNamedParameterJdbcTemplate;
    }

    public Long getUserId(String username) {
        Long userId;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username.strip().toUpperCase());

        try {
            userId = oracleNamedParameterJdbcTemplate.queryForObject(GET_USER_ID_QUERY, params, Long.class);
        } catch (Exception e) {
            String errorMessage = String.format("No UserId found with username = %s", username);
            logger.info(errorMessage);
//            logger.info("No UserId found with username = {}", username);
            throw new ResourceNotFoundException(errorMessage);
        }

        return userId;
    }

    public List<Template> searchForTemplates(String title, String type, String username) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        String baseQuery = "SELECT TEMPLATES_ID, TITLE, AUTHOR, VERSION, CREATE_DT, LAST_MOD_DT FROM HEALTHCARE.TEMPLATES WHERE IS_DELETED = 0 AND IS_PUBLISHED = 1 ";
        StringBuilder stringBuilder = new StringBuilder(baseQuery);

        if (title != null && !title.isBlank()) {
            params.addValue("title", "%" + title.strip().toUpperCase() + "%");
            stringBuilder.append("AND UPPER(TRIM(TITLE)) LIKE :title");
        }

        if (type != null && !type.isBlank()) {
            Integer templateTypeId = getTemplateTypeId(TemplateType.valueOf(type.strip().toUpperCase()));
            params.addValue("templateTypeId", templateTypeId);
            stringBuilder.append("AND TEMPLATE_TYPE_ID = :templateTypeId ");
        }

        if (username != null && !username.isBlank()) {
            Long userId = getUserId(username);
            params.addValue("userId", userId);
            stringBuilder.append("AND USER_ID = :userId");
        }

        return oracleNamedParameterJdbcTemplate.query(stringBuilder.toString(), params, TEMPLATE_ROW_MAPPER);
    }

    public Integer getTemplateCount(String templateType, Long userId, long[] templateIdsFromRequest) {

//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("startDate", startPeriod.toDate(), Types.DATE);
//        params.addValue("endDate", endPeriod.toDate(), Types.DATE);
//        params.addValue("providerIds", providerIds);

        MapSqlParameterSource params = new MapSqlParameterSource();

        List<Long> templateIds = new ArrayList<>();
        for (long id : templateIdsFromRequest) {
            templateIds.add(id);
        }

        params.addValue("templateType", templateType);
        params.addValue("templateIds", templateIds);

        String query =
                "SELECT COUNT(TEMPLATES_ID) AS MY_COUNT " +
                        "FROM HEALTHCARE.TEMPLATES t JOIN HEALTHCARE.TEMPLATE_TYPE tt ON t.TEMPLATE_TYPE_ID = tt.TEMPLATE_TYPE_ID " +
                        "WHERE UPPER(tt.NAME) = :templateType AND TEMPLATES_ID IN (:templateIds)";

        if (userId != null) {
            query += " AND USER_ID = :userId";
            params.addValue("userId", userId);
        }

        return oracleNamedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
    }

    public List<Template> getTemplatesByIds(String templateType, Long userId, long[] templateIdsFromRequest) {

//        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//        namedParameters.addValue("startDate", startPeriod.toDate(), Types.DATE);
//        namedParameters.addValue("endDate", endPeriod.toDate(), Types.DATE);
//        namedParameters.addValue("providerIds", providerIds);

        MapSqlParameterSource params = new MapSqlParameterSource();

        List<Long> templateIds = new ArrayList<>();
        for (long id : templateIdsFromRequest) {
            templateIds.add(id);
        }

        params.addValue("templateType", templateType.toUpperCase());
        params.addValue("templateIds", templateIds);

        String query =
                "SELECT TEMPLATES_ID, AUTHOR, VERSION, TITLE, CREATE_DT, LAST_MOD_DT " +
                        "FROM HEALTHCARE.TEMPLATES t JOIN HEALTHCARE.TEMPLATE_TYPE tt ON t.TEMPLATE_TYPE_ID = tt.TEMPLATE_TYPE_ID " +
                        "WHERE UPPER(tt.NAME) = :templateType AND TEMPLATES_ID IN (:templateIds)";

        if (userId != null) {
            query += " AND USER_ID = :userId";
            params.addValue("userId", userId);
        }

        return oracleNamedParameterJdbcTemplate.query(query, params, TEMPLATE_ROW_MAPPER);
    }


    public List<TemplateSection> getTemplateSections(long templateId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);
        return oracleNamedParameterJdbcTemplate.query(GET_TEMPLATE_SECTIONS_QUERY, params, TEMPLATE_SECTION_ROW_MAPPER);
    }

    public List<TemplateCarePlan> getTemplateCarePlans(long templateId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);
        return oracleNamedParameterJdbcTemplate.query(GET_TEMPLATE_CAREPLANS_QUERY, params, TEMPLATE_CAREPLAN_ROW_MAPPER);
    }

    public List<TemplateSpecialty> getTemplateSpecialties(long templateId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);
        return oracleNamedParameterJdbcTemplate.query(GET_TEMPLATE_SPECIALTIES_QUERY, params, TEMPLATE_SPECIALTY_ROW_MAPPER);
    }

    public Integer getTemplateTypeId(TemplateType templateType) {
        switch (templateType) {
            case SYSTEM:
            case CUSTOM:
                break;
            default:
                String errorMessage = String.format("Unrecognized templateType Name: %s", templateType);
                throw new IllegalArgumentException(errorMessage);
        }

        String query = "SELECT TEMPLATE_TYPE_ID FROM HEALTHCARE.TEMPLATE_TYPE WHERE UPPER(NAME) = :templateType";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateType", templateType.name());

        return oracleNamedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
    }

    public Long getNextSequence(OracleTableName oracleTableName) {
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

        MapSqlParameterSource params = new MapSqlParameterSource();
        return oracleNamedParameterJdbcTemplate.queryForObject(query, params, Long.class);
    }

    public boolean insertTemplate(Long templateId, Template template, Long userId, Integer templateTypeId) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);
        params.addValue("title", template.getTitle());
        params.addValue("author", template.getAuthor());
        params.addValue("version", template.getVersion());
        params.addValue("userId", userId);
        params.addValue("templateTypeId", templateTypeId);

        int status = oracleNamedParameterJdbcTemplate.update(INSERT_TEMPLATE_QUERY, params);

        return true;
    }

    public boolean insertTemplateSection(long templateId, TemplateSection templateSection) {

        long newTemplateSectionId = getNextSequence(OracleTableName.TEMPLATE_SECTIONS);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", newTemplateSectionId);
        params.addValue("templateId", templateId);
        params.addValue("sectionMetaData", templateSection.getSectionMetaData());
        params.addValue("inherit", templateSection.getInherit());
        params.addValue("key", templateSection.getKey());

        int status = oracleNamedParameterJdbcTemplate.update(INSERT_TEMPLATE_SECTION_QUERY, params);

        return true;
    }

    public boolean insertTemplateCarePlan(long templateId, TemplateCarePlan templateCarePlan) {

        long newTemplateCarePlanId = getNextSequence(OracleTableName.TEMPLATE_CAREPLANS);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", newTemplateCarePlanId);
        params.addValue("templateId", templateId);
        params.addValue("carePlanMetaData", templateCarePlan.getCarePlanMetaData());
        params.addValue("inherit", templateCarePlan.getInherit());
        params.addValue("key", templateCarePlan.getKey());

        int status = oracleNamedParameterJdbcTemplate.update(INSERT_TEMPLATE_CAREPLAN_QUERY, params);

        return true;
    }

    public boolean insertTemplateSpecialty(long templateId, TemplateSpecialty templateSpecialty) {

        long newTemplateSpecialtyId = getNextSequence(OracleTableName.TEMPLATE_SPECIALTIES);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", newTemplateSpecialtyId);
        params.addValue("templateId", templateId);
        params.addValue("description", templateSpecialty.getDescription());

        int status = oracleNamedParameterJdbcTemplate.update(INSERT_TEMPLATE_SPECIALTY_QUERY, params);

        return true;
    }

    public boolean updateTemplateMetadata(Template existingTemplate, UpdateTemplateMetadataRequest updateTemplateMetadataRequest) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", updateTemplateMetadataRequest.getExistingTemplateId());

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

        int status = oracleNamedParameterJdbcTemplate.update(UPDATE_TEMPLATE_METADATA_QUERY, params);

        return true;
    }

    public boolean insertRequest(PostgresTableName postgresTableName, String request) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("request", request);

        // Not vulnerable to SQL Injection here because tableName comes from internally
        String query = "INSERT INTO " + postgresTableName.getName() + " (data) VALUES (:request::jsonb)";

        int status = postgresNamedParameterJdbcTemplate.update(query, params);

        return true;
    }

    public Long getNextPostgresSequence() {
        return postgresNamedParameterJdbcTemplate.queryForObject(GET_NEXT_POSTGRES_SEQUENCE, new MapSqlParameterSource(), Long.class);
    }

    // ---------------------------
    // SQL QUERIES AND MAPPERS
    // ---------------------------

    private static final String GET_NEXT_POSTGRES_SEQUENCE = "SELECT nextval('sequence_number')";

    private static final String GET_USER_ID_QUERY = "SELECT USERID FROM REG.USERS WHERE UPPER(TRIM(USERNAME)) = :username";

    private final RowMapper<Template> TEMPLATE_ROW_MAPPER = (rs, i) -> {
        Template template = new Template();

        template.setId(rs.getLong("TEMPLATES_ID"));
        template.setAuthor(rs.getString("AUTHOR"));
        template.setVersion(rs.getString("VERSION"));
        template.setTitle(rs.getString("TITLE"));
        template.setCreatedOn(rs.getString("CREATE_DT"));           // TODO: check if Date parsing works. Right now, I'm inserting SYSDATE, not these
        template.setUpdatedOn(rs.getString("LAST_MOD_DT"));

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

}
