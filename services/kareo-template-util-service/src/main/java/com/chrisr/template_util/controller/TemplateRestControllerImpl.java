package com.chrisr.template_util.controller;

import com.chrisr.template_util.exception.BadRequestException;
import com.chrisr.template_util.exception.IllegalArgumentException;
import com.chrisr.template_util.repository.entity.template.Template;
import com.chrisr.template_util.repository.enums.PostgresTableName;
import com.chrisr.template_util.repository.enums.TemplateType;
import com.chrisr.template_util.request.CopyTemplatesRequest;
import com.chrisr.template_util.request.SearchForTemplateRequest;
import com.chrisr.template_util.request.UpdateTemplateMetadataRequest;
import com.chrisr.template_util.response.ApiResponse;
import com.chrisr.template_util.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@RestController
public class TemplateRestControllerImpl implements TemplateRestController {

    private static final Logger logger = LoggerFactory.getLogger(TemplateRestControllerImpl.class);

    // 1. GET templates?title=xxx?username=xxx				- title || (username | system)
    // 2. POST templates/copy-user-template-to-user			- fromUser | toUser | title
    // 3. POST templates/copy-user-template-to-system		- fromUser | title | (existingSystemTemplateTitle to replace)
    // 4. POST templates/update-system-template-metadata	- existingTitle | existingAuthor | existingVersion || newTitle | newAuthor | newVersion


//	@Autowired
//	private UserService userService;

    private final TemplateService templateService;

    @Autowired
    TemplateRestControllerImpl(TemplateService templateService) {
        this.templateService = templateService;
    }


    // TODO: add paging to search results

    // TODO: add "author" to the query

    @Override
    public ResponseEntity<List<Template>> searchForTemplates(@RequestParam(required = false) String title,
                                                             @RequestParam(required = false) String type,
                                                             @RequestParam(required = false) String username) {

        if ((title == null || title.isBlank()) && (type == null || type.isBlank()) && (username == null || username.isBlank())) {
            String errorMessage = "At least one of the following query parameters is required: 'title', 'type', or 'username'   (ex) /api/templates?title=MedSpa&type=SYSTEM";
            throw new BadRequestException(errorMessage);
        }

        if ("SYSTEM".equalsIgnoreCase(type) && !(username == null || username.isBlank())) {
            String errorMessage = "When the type is SYSTEM, username cannot be provided.";
            throw new BadRequestException(errorMessage);
        }

        if ("USER".equalsIgnoreCase(type) && (username == null || username.isBlank())) {
            String errorMessage = "When the type is USER, username must be provided.";
            throw new BadRequestException(errorMessage);
        }

        if (title != null && title.strip().length() < 3) {
            String errorMessage = "Title must be at least 3 characters or more.";
            throw new BadRequestException(errorMessage);
        }

        // TODO: wrap results in a response object, which includes "totalCount", "count", "pageSize", "page"

        List<Template> results = templateService.searchForTemplates(title, type, username);

        SearchForTemplateRequest searchForTemplateRequest = new SearchForTemplateRequest();
        searchForTemplateRequest.setTitle(title);
        searchForTemplateRequest.setType(type);
        searchForTemplateRequest.setUsername(username);

        templateService.storeRequest(PostgresTableName.REQUEST_HISTORY, searchForTemplateRequest);

        return ResponseEntity.ok().body(results);
    }

    @Override
    public ResponseEntity<ApiResponse> copyTemplates(@Valid @RequestBody CopyTemplatesRequest copyTemplatesRequest) {
        // @Valid annotation is REQUIRED in conjunction with the RequestBody class annotations such as @NotNull @NotBlank @NotEmpty

		/*
		{
			"fromEnvironment": "DEV",
			"toEnvironment": "DEV",
			"fromType": "SYSTEM",
			"toType": "USER",
			"fromUsername": "",
			"toUsername": "amy@kareo.com",
			"templateIds": [3]
		}
		 */

        System.out.println("----------   copyTemplatesRequest   ---------");
        System.out.println(copyTemplatesRequest);


        String fromType = copyTemplatesRequest.getFromType().strip();
        String toType = copyTemplatesRequest.getToType().strip();

        if ("SYSTEM".equalsIgnoreCase(fromType) && !(copyTemplatesRequest.getFromUsername() == null || copyTemplatesRequest.getFromUsername().isBlank())) {
            throw new BadRequestException("When the 'fromType' is SYSTEM, 'fromUsername' cannot be provided.");
        }

        if ("SYSTEM".equalsIgnoreCase(toType) && !(copyTemplatesRequest.getToUsername() == null || copyTemplatesRequest.getToUsername().isBlank())) {
            throw new BadRequestException("When the 'toType' is SYSTEM, 'toUsername' cannot be provided.");
        }

        if ("USER".equalsIgnoreCase(fromType) && (copyTemplatesRequest.getFromUsername() == null || copyTemplatesRequest.getFromUsername().isBlank())) {
            throw new BadRequestException("When the 'fromType' is USER, 'fromUsername' is required.");
        }

        if ("USER".equalsIgnoreCase(toType) && (copyTemplatesRequest.getToUsername() == null || copyTemplatesRequest.getToUsername().isBlank())) {
            throw new BadRequestException("When the 'toType' is USER, 'toUsername' is required.");
        }

        if (copyTemplatesRequest.getTemplateIds() == null || copyTemplatesRequest.getTemplateIds().length == 0) {
            throw new IllegalArgumentException("TemplateIDs is required and cannot be blank");
        }

        if ("SYSTEM".equalsIgnoreCase(fromType) && "USER".equalsIgnoreCase(toType)) {

            Long fromUserId = null;
            Long toUserId = templateService.getUserId(copyTemplatesRequest.getToUsername());
            System.out.println("toUserId = " + toUserId);

            Integer templateCount = templateService.getTemplateCount(fromType, fromUserId, copyTemplatesRequest.getTemplateIds());
            System.out.println("templateCount = " + templateCount);

            if (templateCount != copyTemplatesRequest.getTemplateIds().length) {
                throw new IllegalArgumentException("One or more System template IDs not found in database. Ensure that the templateIDs are correct");
            }

            List<Template> templates = templateService.getTemplatesByIds(fromType, fromUserId, copyTemplatesRequest.getTemplateIds());
            System.out.println(templates.toString());

            for (Template template : templates) {
                templateService.copyTemplate(template, TemplateType.CUSTOM, toUserId);
            }

            templateService.storeRequest(PostgresTableName.REQUEST_HISTORY, copyTemplatesRequest);

            int totalCount = templates.size();
            return ResponseEntity.ok().body(new ApiResponse(true, totalCount + " System template(s) have been successfully copied to user (" + copyTemplatesRequest.getToUsername() + ")"));

        } else if ("USER".equalsIgnoreCase(fromType) && "SYSTEM".equalsIgnoreCase(toType)) {

            // TODO: implement systemTemplateIdToReplace | createNewSystemTemplate use-case


            Long fromUserId = templateService.getUserId(copyTemplatesRequest.getFromUsername());
            System.out.println("fromUserId = " + fromUserId);
            Long toUserId = null;

            Integer templateCount = templateService.getTemplateCount(fromType, fromUserId, copyTemplatesRequest.getTemplateIds());
            System.out.println("templateCount = " + templateCount);

            if (templateCount != copyTemplatesRequest.getTemplateIds().length) {
                String errorMessage = String.format("One or more template IDs not found in database for user %s. Ensure that the templateIDs are correct", copyTemplatesRequest.getFromUsername());
                throw new IllegalArgumentException(errorMessage);
            }

            List<Template> templates = templateService.getTemplatesByIds(fromType, fromUserId, copyTemplatesRequest.getTemplateIds());
            System.out.println(templates.toString());

            for (Template template : templates) {
                templateService.copyTemplate(template, TemplateType.SYSTEM, toUserId);
            }

            templateService.storeRequest(PostgresTableName.REQUEST_HISTORY, copyTemplatesRequest);

            int totalCount = templates.size();
            return ResponseEntity.ok().body(new ApiResponse(true, totalCount + " User (" + copyTemplatesRequest.getFromUsername() + ") template(s) have been successfully copied to System templates."));

        } else if ("USER".equalsIgnoreCase(fromType) && "USER".equalsIgnoreCase(toType)) {

            Long fromUserId = templateService.getUserId(copyTemplatesRequest.getFromUsername());
            System.out.println("fromUserId = " + fromUserId);
            Long toUserId = templateService.getUserId(copyTemplatesRequest.getToUsername());
            System.out.println("toUserId = " + toUserId);

            Integer templateCount = templateService.getTemplateCount(fromType, fromUserId, copyTemplatesRequest.getTemplateIds());
            System.out.println("templateCount = " + templateCount);

            if (templateCount != copyTemplatesRequest.getTemplateIds().length) {
                String errorMessage = String.format("One or more template IDs not found in database for user %s. Ensure that the templateIDs are correct", copyTemplatesRequest.getFromUsername());
                throw new IllegalArgumentException(errorMessage);
            }

            List<Template> templates = templateService.getTemplatesByIds(fromType, fromUserId, copyTemplatesRequest.getTemplateIds());
            System.out.println(templates.toString());

            for (Template template : templates) {
                templateService.copyTemplate(template, TemplateType.CUSTOM, toUserId);
            }

            templateService.storeRequest(PostgresTableName.REQUEST_HISTORY, copyTemplatesRequest);

            int totalCount = templates.size();
            return ResponseEntity.ok().body(new ApiResponse(true, totalCount + " User (" + copyTemplatesRequest.getFromUsername() + ") template(s) have been successfully copied to user (" + copyTemplatesRequest.getToUsername() + ")"));

        } else {
            throw new BadRequestException("System templates cannot be copied from/to System templates.");
        }
    }

    @Override
    public ResponseEntity<ApiResponse> updateTemplateMetadata(@Valid @RequestBody UpdateTemplateMetadataRequest updateTemplateMetadataRequest) {

        System.out.println("---------------------");
        System.out.println(updateTemplateMetadataRequest.toString());

        if ((updateTemplateMetadataRequest.getNewTitle() == null || updateTemplateMetadataRequest.getNewTitle().isBlank()) &&
            (updateTemplateMetadataRequest.getNewAuthor() == null || updateTemplateMetadataRequest.getNewAuthor().isBlank()) &&
            (updateTemplateMetadataRequest.getNewVersion() == null || updateTemplateMetadataRequest.getNewVersion().isBlank())) {
            throw new BadRequestException("At least one of the following fields is required for the new template: Title, Author, or Version");
        }

        // simply update the row in-place. I store the request in postgres DB so rollback is possible if needed
        templateService.updateTemplateMetadata(updateTemplateMetadataRequest);

        templateService.storeRequest(PostgresTableName.REQUEST_HISTORY, updateTemplateMetadataRequest);

        return ResponseEntity.ok().body(new ApiResponse(true, "Template has been successfully updated"));
    }

    @Override
    public ResponseEntity<ApiResponse> databaseDemo(@RequestBody CopyTemplatesRequest copyTemplatesRequest) {

        System.out.println("----------   copyTemplatesRequest   ---------");
        System.out.println(copyTemplatesRequest);


        return null;
    }
}
