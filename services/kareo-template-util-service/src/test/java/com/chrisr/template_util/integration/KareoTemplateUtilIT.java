package com.chrisr.template_util.integration;

import com.chrisr.template_util.response.ErrorResponse;
import com.chrisr.template_util.request.UpdateTemplateMetadataRequest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KareoTemplateUtilIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    int randomPort;


    // TODO: add @Before hook to sign up for an account and login and get JWT token



    @Test
    public void testUpdateTemplateMetadata_shouldFailWhenExistingTemplateIdDoesNotExist() throws URISyntaxException {

        final String uriString = "http://localhost:" + randomPort + "/api/templates/update-system-template-metadata";
        URI uri = new URI(uriString);

        // -----  POST  -----
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        UpdateTemplateMetadataRequest updateTemplateMetadataRequest = new UpdateTemplateMetadataRequest();
        updateTemplateMetadataRequest.setExistingTemplateId(137L);          // not existing template id
        updateTemplateMetadataRequest.setNewTitle("MedSpa Therapy");
        updateTemplateMetadataRequest.setNewAuthor("Chris Ro");
        updateTemplateMetadataRequest.setNewVersion("2.0");

        HttpEntity<UpdateTemplateMetadataRequest> request = new HttpEntity<>(updateTemplateMetadataRequest, httpHeaders);

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(uri, request, ErrorResponse.class);

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Template Not Found with ID = 137", errorResponse.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Ignore
    @Test
    public void testUpdateTemplateMetadata_shouldSucceedIfRequestIsValid() throws URISyntaxException {

        // TODO: add before hook to get a System template ID to copy over?
        //  or create a new temp System template and then delete it afterward?


        final String uriString = "http://localhost:" + randomPort + "/api/templates/update-system-template-metadata";
        URI uri = new URI(uriString);

        // -----  POST  -----
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        UpdateTemplateMetadataRequest updateTemplateMetadataRequest = new UpdateTemplateMetadataRequest();
        updateTemplateMetadataRequest.setExistingTemplateId(137L);          // Should be existing template id
        updateTemplateMetadataRequest.setNewTitle("MedSpa Therapy");
        updateTemplateMetadataRequest.setNewAuthor("Chris Ro");
        updateTemplateMetadataRequest.setNewVersion("2.0");

        HttpEntity<UpdateTemplateMetadataRequest> request = new HttpEntity<>(updateTemplateMetadataRequest, httpHeaders);


//        ResponseEntity<ApiResponse> response = testRestTemplate.postForEntity(uri, request, ApiResponse.class);

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(uri, request, ErrorResponse.class);

        System.out.println("-----------------------");
        System.out.println(response.getBody());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Template Not Found with ID = 137", errorResponse.getMessage());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

//        assertEquals(HttpStatus.OK, response.getStatusCode());

//        ApiResponse apiResponse = response.getBody();

//        assertNotNull(apiResponse);
//        assertTrue(apiResponse.getSuccess());

        // TODO: need to validate in DB that the new template metadata exists?


        // TODO: need to add clean up task? (revert back the change, and/or remove the newly created templates from DB)

    }
}
