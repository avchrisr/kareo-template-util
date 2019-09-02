package com.chrisr.template_util.repository.entity.template;

import com.chrisr.template_util.request.SearchForTemplateRequest;

public class SearchRequest extends BaseRequest {

    private SearchForTemplateRequest searchForTemplateRequest;

    public SearchRequest(String username) {
        super("SEARCH", username);
    }

    public SearchForTemplateRequest getSearchForTemplateRequest() {
        return searchForTemplateRequest;
    }

    public void setSearchForTemplateRequest(SearchForTemplateRequest searchForTemplateRequest) {
        this.searchForTemplateRequest = searchForTemplateRequest;
    }

    @Override
    public String toString() {
        return super.toString() + " | SearchRequest{" +
                "searchForTemplateRequest=" + searchForTemplateRequest +
                '}';
    }
}
