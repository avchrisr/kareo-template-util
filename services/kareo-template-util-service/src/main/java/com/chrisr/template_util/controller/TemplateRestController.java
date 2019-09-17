package com.chrisr.template_util.controller;

import com.chrisr.template_util.repository.entity.template.Template;
import com.chrisr.template_util.request.CopyTemplatesRequest;
import com.chrisr.template_util.request.UpdateTemplateMetadataRequest;
import com.chrisr.template_util.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/templates")
public interface TemplateRestController {

	@GetMapping
	ResponseEntity<List<Template>> searchForTemplates(@RequestParam(required = false) String title,
		 @RequestParam(name = "find-partial-title-matches", required = false) String findPartialTitleMatches,
		 @RequestParam(required = false) String type,
		 @RequestParam(required = false) String author,
		 @RequestParam(required = false) String version,
		 @RequestParam(required = false) String username);

	@PostMapping("/copy-templates")
	ResponseEntity<ApiResponse> copyTemplates(@Valid @RequestBody CopyTemplatesRequest copyTemplatesRequest);

	@PostMapping("/update-template-metadata")
	ResponseEntity<ApiResponse> updateTemplateMetadata(@Valid @RequestBody UpdateTemplateMetadataRequest updateTemplateMetadataRequest);


	@PostMapping("/databaseDemo")
	ResponseEntity<ApiResponse> databaseDemo(@RequestBody CopyTemplatesRequest copyTemplatesRequest);
}
