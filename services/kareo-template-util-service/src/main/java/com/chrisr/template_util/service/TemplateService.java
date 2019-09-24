package com.chrisr.template_util.service;

import com.chrisr.template_util.exception.AppException;
import com.chrisr.template_util.exception.ResourceNotFoundException;
import com.chrisr.template_util.repository.entity.template.*;
import com.chrisr.template_util.repository.enums.OracleTableName;
import com.chrisr.template_util.repository.enums.PostgresTableName;
import com.chrisr.template_util.repository.enums.TemplateType;
import com.chrisr.template_util.repository.TemplateRepository;
import com.chrisr.template_util.request.CopyTemplatesRequest;
import com.chrisr.template_util.request.SearchForTemplateRequest;
import com.chrisr.template_util.request.UpdateTemplateMetadataRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    TemplateService(TemplateRepository templateRepository, ObjectMapper objectMapper) {
        this.templateRepository = templateRepository;
        this.objectMapper = objectMapper;
    }


    public Long getUserId(String username) {
        return templateRepository.getUserId(username);
    }

    @Transactional(readOnly = true)
    public List<Template> searchForTemplates(String environment, String templateId, String title, String findPartialTitleMatches, String type, String author, String version, String username) {
        return templateRepository.searchForTemplates(environment, templateId, title, findPartialTitleMatches, type, author, version, username);
    }

    @Transactional(readOnly = true)
    public Integer getTemplateCount(String templateType, Long userId, long[] templateIds) {
        return templateRepository.getTemplateCount(templateType, userId, templateIds);
    }

    @Transactional(readOnly = true)
    public List<Template> getTemplatesByIds(String templateType, Long userId, long[] templateIdsFromRequest) {
        return templateRepository.getTemplatesByIds(templateType, userId, templateIdsFromRequest);
    }

    @Transactional
    public long copyTemplate(Template template, TemplateType toTemplateType, Long toUserId) {
        // gather
        List<TemplateSection> templateSections = templateRepository.getTemplateSections(template.getId());
        System.out.println(templateSections.toString());

        List<TemplateCarePlan> templateCarePlans = templateRepository.getTemplateCarePlans(template.getId());
        System.out.println(templateCarePlans.toString());

        List<TemplateSpecialty> templateSpecialties = templateRepository.getTemplateSpecialties(template.getId());
        System.out.println(templateSpecialties.toString());

        // insert
        Integer toTemplateTypeId = templateRepository.getTemplateTypeId(toTemplateType);
        Long newTemplateId = templateRepository.getNextSequence(OracleTableName.TEMPLATES);
        templateRepository.insertTemplate(newTemplateId, template, toUserId, toTemplateTypeId);

        for (TemplateSection templateSection : templateSections) {
            templateRepository.insertTemplateSection(newTemplateId, templateSection);
        }

        for (TemplateCarePlan templateCarePlan : templateCarePlans) {
            templateRepository.insertTemplateCarePlan(newTemplateId, templateCarePlan);
        }

        for (TemplateSpecialty templateSpecialty : templateSpecialties) {
            templateRepository.insertTemplateSpecialty(newTemplateId, templateSpecialty);
        }

        return newTemplateId;
    }

    @Transactional
    public void replaceTemplate(long templateIdToReplace, Template template, TemplateType toTemplateType, Long toUserId) {
        long newTemplateId = copyTemplate(template, toTemplateType, toUserId);
        templateRepository.deleteTemplate(templateIdToReplace);
        templateRepository.replaceUserDeactivatedTemplateId(templateIdToReplace, newTemplateId);
    }

    @Transactional
    public void updateTemplateMetadata(UpdateTemplateMetadataRequest updateTemplateMetadataRequest) {

        // get the existing template
        long[] templateIds = {updateTemplateMetadataRequest.getCurrentTemplateId()};
        List<Template> templates = templateRepository.getTemplatesByIds(null, null, templateIds);
        if (templates.isEmpty()) {
            String errorMessage = String.format("Template Not Found with ID = %s", updateTemplateMetadataRequest.getCurrentTemplateId());
            throw new ResourceNotFoundException(errorMessage);
        }

        Template existingTemplate = templates.get(0);
        if (!existingTemplate.getTitle().equalsIgnoreCase(updateTemplateMetadataRequest.getCurrentTemplateTitle())) {
            // validate that the title in the existing template matches the current title provided in the request
            String errorMessage = String.format("Template title provided does not match the existing template's title with ID = %s", updateTemplateMetadataRequest.getCurrentTemplateId());
            throw new ResourceNotFoundException(errorMessage);
        }

        templateRepository.updateTemplateMetadata(existingTemplate, updateTemplateMetadataRequest);
    }

    @Transactional
    public void storeRequest(PostgresTableName postgresTableName, SearchForTemplateRequest searchForTemplateRequest) {
        Long id = templateRepository.getNextPostgresSequence();

        SearchRequest searchRequest = new SearchRequest(getLoggedInUsername());
        searchRequest.setId(id);
        searchRequest.setSearchForTemplateRequest(searchForTemplateRequest);

        try {
            templateRepository.insertRequest(postgresTableName, objectMapper.writeValueAsString(searchRequest));
        } catch (JsonProcessingException e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    @Transactional
    public void storeRequest(PostgresTableName postgresTableName, CopyTemplatesRequest copyTemplatesRequest) {
        Long id = templateRepository.getNextPostgresSequence();

        CopyRequest copyRequest = new CopyRequest(getLoggedInUsername());
        copyRequest.setId(id);
        copyRequest.setCopyTemplatesRequest(copyTemplatesRequest);

        try {
            templateRepository.insertRequest(postgresTableName, objectMapper.writeValueAsString(copyRequest));
        } catch (JsonProcessingException e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    @Transactional
    public void storeRequest(PostgresTableName postgresTableName, UpdateTemplateMetadataRequest updateTemplateMetadataRequest) {
        Long id = templateRepository.getNextPostgresSequence();

        UpdateRequest updateRequest = new UpdateRequest(getLoggedInUsername());
        updateRequest.setId(id);
        updateRequest.setUpdateTemplateMetadataRequest(updateTemplateMetadataRequest);

        try {
            templateRepository.insertRequest(postgresTableName, objectMapper.writeValueAsString(updateRequest));
        } catch (JsonProcessingException e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    private String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loggedInUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return loggedInUser.getUsername();
    }
}
