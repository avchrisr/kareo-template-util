package com.chrisr.template_util.repository.entity.template;

import com.chrisr.template_util.request.UpdateTemplateMetadataRequest;

public class UpdateRequest extends BaseRequest {

    private UpdateTemplateMetadataRequest updateTemplateMetadataRequest;

    public UpdateRequest(String username) {
        super("UPDATE", username);
    }

    public UpdateTemplateMetadataRequest getUpdateTemplateMetadataRequest() {
        return updateTemplateMetadataRequest;
    }

    public void setUpdateTemplateMetadataRequest(UpdateTemplateMetadataRequest updateTemplateMetadataRequest) {
        this.updateTemplateMetadataRequest = updateTemplateMetadataRequest;
    }

    @Override
    public String toString() {
        return super.toString() + " | UpdateRequest{" +
                "updateTemplateMetadataRequest=" + updateTemplateMetadataRequest +
                '}';
    }
}
