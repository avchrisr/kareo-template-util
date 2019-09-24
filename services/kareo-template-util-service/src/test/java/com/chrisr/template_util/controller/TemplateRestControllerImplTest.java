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
        templateRestController.searchForTemplates("","", "", "", "", "", "", "");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_SystemTypeWithUsername_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("dev","therapy", "","SYSTEM", "", "","amy@kareo.com", "");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_TitleWithTwoCharacters_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("dev","BE", "", "", "", "", "", "");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_NoTitleWithPartialTitleMatchOptionOn_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("dev","", "true", "", "", "", "", "");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_findPartialTitleMatchesOptionValueOtherThanTrueOrFalse_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("dev","Acne", "invalidValue", "", "", "", "", "");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_invalidTemplateId_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("dev","Acne", "invalidValue", "", "", "", "", "123A");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_MissingEnvironment_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("","Med Spa", "", "USER", "", "", "amy@kareo.com", "123");
    }

    @Test(expected = BadRequestException.class)
    public void searchForTemplates_InvalidEnvironmentName_ShouldThrowBadRequestException() {
        templateRestController.searchForTemplates("myCustomEnv","Med Spa", "", "USER", "", "", "amy@kareo.com", "");
    }

    @Test
    public void searchForTemplates_UserTypeWithoutUsername_ShouldSucceed() {
        templateRestController.searchForTemplates("dev","therapy", "", "USER", "", "", "", "");
    }

    @Test
    public void searchForTemplates_ValidTemplateIdRequest_ShouldSucceed() {
        templateRestController.searchForTemplates("dev","Med Spa", "", "USER", "", "", "amy@kareo.com", "123");
    }

    @Test
    public void searchForTemplates_ValidRequest_ShouldSucceed() {
        templateRestController.searchForTemplates("dev","Med Spa", "", "USER", "", "", "amy@kareo.com", "");
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
    public void updateTemplateMetadata_NoEnvironmentProvided_ShouldThrowBadRequestException() {

        UpdateTemplateMetadataRequest updateTemplateMetadataRequest = new UpdateTemplateMetadataRequest();
        updateTemplateMetadataRequest.setCurrentTemplateId(100L);
        updateTemplateMetadataRequest.setCurrentTemplateTitle("Med Spa v1");
        templateRestController.updateTemplateMetadata(updateTemplateMetadataRequest);
    }

    @Test(expected = BadRequestException.class)
    public void updateTemplateMetadata_NoCurrentTemplateIdProvided_ShouldThrowBadRequestException() {

        UpdateTemplateMetadataRequest updateTemplateMetadataRequest = new UpdateTemplateMetadataRequest();
        updateTemplateMetadataRequest.setEnvironment("dev");
        updateTemplateMetadataRequest.setCurrentTemplateTitle("Med Spa v1");
        templateRestController.updateTemplateMetadata(updateTemplateMetadataRequest);
    }

    @Test(expected = BadRequestException.class)
    public void updateTemplateMetadata_NoCurrentTemplateTitleProvided_ShouldThrowBadRequestException() {

        UpdateTemplateMetadataRequest updateTemplateMetadataRequest = new UpdateTemplateMetadataRequest();
        updateTemplateMetadataRequest.setEnvironment("dev");
        updateTemplateMetadataRequest.setCurrentTemplateId(100L);
        templateRestController.updateTemplateMetadata(updateTemplateMetadataRequest);
    }

    @Test(expected = BadRequestException.class)
    public void updateTemplateMetadata_NoNewInfoProvided_ShouldThrowBadRequestException() {

        UpdateTemplateMetadataRequest updateTemplateMetadataRequest = new UpdateTemplateMetadataRequest();
        updateTemplateMetadataRequest.setEnvironment("dev");
        updateTemplateMetadataRequest.setCurrentTemplateId(100L);
        updateTemplateMetadataRequest.setCurrentTemplateTitle("Med Spa v1");
        templateRestController.updateTemplateMetadata(updateTemplateMetadataRequest);
    }

    @Test
    public void updateTemplateMetadata_ValidRequest_ShouldSucceed() {

        UpdateTemplateMetadataRequest updateTemplateMetadataRequest = new UpdateTemplateMetadataRequest();
        updateTemplateMetadataRequest.setEnvironment("dev");
        updateTemplateMetadataRequest.setCurrentTemplateId(100L);
        updateTemplateMetadataRequest.setCurrentTemplateTitle("Med Spa v1");
        updateTemplateMetadataRequest.setNewTitle("Med Spa v2");
        templateRestController.updateTemplateMetadata(updateTemplateMetadataRequest);
    }
}
