package com.chrisr.template_util.repository.entity.template;

import com.chrisr.template_util.request.CopyTemplatesRequest;

public class CopyRequest extends BaseRequest {

    private CopyTemplatesRequest copyTemplatesRequest;

    public CopyRequest(String username) {
        super("COPY", username);
    }

    public CopyTemplatesRequest getCopyTemplatesRequest() {
        return copyTemplatesRequest;
    }

    public void setCopyTemplatesRequest(CopyTemplatesRequest copyTemplatesRequest) {
        this.copyTemplatesRequest = copyTemplatesRequest;
    }

    @Override
    public String toString() {
        return super.toString() + " | CopyRequest{" +
                "copyTemplatesRequest=" + copyTemplatesRequest +
                '}';
    }
}
