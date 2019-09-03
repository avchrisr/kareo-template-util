package com.chrisr.template_util.controller;

import com.chrisr.template_util.exception.BadRequestException;
import com.chrisr.template_util.exception.IllegalArgumentException;
import com.chrisr.template_util.request.CopyTemplatesRequest;
import com.chrisr.template_util.request.UpdateTemplateMetadataRequest;
import com.chrisr.template_util.service.TemplateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemplateRestControllerImplTest {

    @Mock
    TemplateService templateService;

    @InjectMocks
    TemplateRestControllerImpl templateRestController;


    @Test(expected = BadRequestException.class)
    public void searchForTemplates_NoParams_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("", "", "", "", "", "");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_SystemTypeWithUsername_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("", "","SYSTEM", "", "","amy@kareo.com");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_UserTypeWithoutUsername_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("", "", "USER", "", "", "");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_TitleWithTwoCharacters_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("BE", "", "", "", "", "");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_NoTitleWithPartialTitleMatchOptionOn_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("", "true", "", "", "", "");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_findPartialTitleMatchesOptionValueOtherThanTrueOrFalse_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("Acne", "invalidValue", "", "", "", "");
    }

    @Test
    public void searchForTemplates_ValidRequest_ShouldSucceed() {
        templateRestController.searchForTemplates("Med Spa", "", "USER", "", "", "amy@kareo.com");
    }

    @Test(expected = BadRequestException.class)
    public void copyTemplates_SystemTypeWithFromUsername_ShouldThrowBadRequestException() {

        CopyTemplatesRequest copyTemplatesRequest = new CopyTemplatesRequest();
        copyTemplatesRequest.setFromType("SYSTEM");
        copyTemplatesRequest.setFromUsername("some_user");
        copyTemplatesRequest.setToType("USER");
        copyTemplatesRequest.setToUsername("some_other_user");

        templateRestController.copyTemplates(copyTemplatesRequest);
    }

    @Test(expected = BadRequestException.class)
    public void copyTemplates_SystemTypeWithToUsername_ShouldThrowBadRequestException() {

        CopyTemplatesRequest copyTemplatesRequest = new CopyTemplatesRequest();
        copyTemplatesRequest.setFromType("USER");
        copyTemplatesRequest.setFromUsername("some_user");
        copyTemplatesRequest.setToType("SYSTEM");
        copyTemplatesRequest.setToUsername("some_other_user");

        templateRestController.copyTemplates(copyTemplatesRequest);
    }

    @Test(expected = BadRequestException.class)
    public void copyTemplates_UserTypeWithMissingUsername_ShouldThrowBadRequestException() {

        CopyTemplatesRequest copyTemplatesRequest = new CopyTemplatesRequest();
        copyTemplatesRequest.setFromType("USER");
        copyTemplatesRequest.setFromUsername("");
        copyTemplatesRequest.setToType("USER");
        copyTemplatesRequest.setToUsername("");

        templateRestController.copyTemplates(copyTemplatesRequest);
    }

    @Test(expected = BadRequestException.class)
    public void copyTemplates_CopyingFromSystemToSystem_ShouldThrowBadRequestException() {

        CopyTemplatesRequest copyTemplatesRequest = new CopyTemplatesRequest();
        copyTemplatesRequest.setFromType("SYSTEM");
        copyTemplatesRequest.setToType("SYSTEM");
        long[] templateIds = {1L, 2L, 3L};
        copyTemplatesRequest.setTemplateIds(templateIds);

        templateRestController.copyTemplates(copyTemplatesRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyTemplates_MissingTemplateIds_ShouldThrowIllegalArgumentException() {

        CopyTemplatesRequest copyTemplatesRequest = new CopyTemplatesRequest();
        copyTemplatesRequest.setFromType("USER");
        copyTemplatesRequest.setFromUsername("user1");
        copyTemplatesRequest.setToType("SYSTEM");

        templateRestController.copyTemplates(copyTemplatesRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyTemplates_TemplateCountDoesNotMatchTheProvidedIds_ShouldThrowIllegalArgumentException() {

        CopyTemplatesRequest copyTemplatesRequest = new CopyTemplatesRequest();
        copyTemplatesRequest.setFromType("USER");
        copyTemplatesRequest.setFromUsername("user1");
        copyTemplatesRequest.setToType("SYSTEM");
        copyTemplatesRequest.setCreateNewSystemTemplate(true);
        long[] templateIds = {1L, 2L, 3L};
        copyTemplatesRequest.setTemplateIds(templateIds);

        when(templateService.getTemplateCount(anyString(), anyLong(), any(long[].class))).thenReturn(2);
        templateRestController.copyTemplates(copyTemplatesRequest);
    }

    @Test(expected = BadRequestException.class)
    public void copyTemplates_SystemTemplateIdToReplaceNotProvidedWhenReplacingOptionChecked_ShouldThrowBadRequestException() {

        CopyTemplatesRequest copyTemplatesRequest = new CopyTemplatesRequest();
        copyTemplatesRequest.setFromType("USER");
        copyTemplatesRequest.setFromUsername("user1");
        copyTemplatesRequest.setToType("SYSTEM");
        copyTemplatesRequest.setCreateNewSystemTemplate(false);
        long[] templateIds = {1L, 2L, 3L};
        copyTemplatesRequest.setTemplateIds(templateIds);

        templateRestController.copyTemplates(copyTemplatesRequest);
    }

    @Test
    public void copyTemplates_ValidRequest_ShouldSucceed() {

        CopyTemplatesRequest copyTemplatesRequest = new CopyTemplatesRequest();
        copyTemplatesRequest.setFromType("USER");
        copyTemplatesRequest.setFromUsername("user1");
        copyTemplatesRequest.setToType("SYSTEM");
        copyTemplatesRequest.setCreateNewSystemTemplate(true);
        long[] templateIds = {1L, 2L, 3L};
        copyTemplatesRequest.setTemplateIds(templateIds);

        when(templateService.getTemplateCount(anyString(), anyLong(), any(long[].class))).thenReturn(3);
        templateRestController.copyTemplates(copyTemplatesRequest);
    }

    @Test(expected = BadRequestException.class)
    public void updateTemplateMetadata_NoNewInfoProvided_ShouldThrowBadRequestException() {

        UpdateTemplateMetadataRequest updateTemplateMetadataRequest = new UpdateTemplateMetadataRequest();
        templateRestController.updateTemplateMetadata(updateTemplateMetadataRequest);
    }

    @Test
    public void updateTemplateMetadata_ValidRequest_ShouldSucceed() {

        UpdateTemplateMetadataRequest updateTemplateMetadataRequest = new UpdateTemplateMetadataRequest();
        updateTemplateMetadataRequest.setExistingTemplateId(50L);
        updateTemplateMetadataRequest.setNewTitle("This is new title");
        templateRestController.updateTemplateMetadata(updateTemplateMetadataRequest);
    }
}
