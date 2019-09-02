package com.chrisr.template_util.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Arrays;

public class CopyTemplatesRequest {

    @NotBlank
    private String fromEnvironment;     // DEV | QA | PROD

    @NotBlank
    private String toEnvironment;

    @NotBlank
    private String fromType;            // USER | SYSTEM

    @NotBlank
    private String toType;

    private String fromUsername;
    private String toUsername;


    // TODO: see if this can be List<Long> instead ?

    @NotEmpty
    private long[] templateIds;

    private boolean createNewSystemTemplate;
    private long systemTemplateIdToReplace;


    public String getFromEnvironment() {
        return fromEnvironment;
    }

    public void setFromEnvironment(String fromEnvironment) {
        this.fromEnvironment = fromEnvironment;
    }

    public String getToEnvironment() {
        return toEnvironment;
    }

    public void setToEnvironment(String toEnvironment) {
        this.toEnvironment = toEnvironment;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getToType() {
        return toType;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public long[] getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(long[] templateIds) {
        this.templateIds = templateIds;
    }

    public boolean isCreateNewSystemTemplate() {
        return createNewSystemTemplate;
    }

    public void setCreateNewSystemTemplate(boolean createNewSystemTemplate) {
        this.createNewSystemTemplate = createNewSystemTemplate;
    }

    public long getSystemTemplateIdToReplace() {
        return systemTemplateIdToReplace;
    }

    public void setSystemTemplateIdToReplace(long systemTemplateIdToReplace) {
        this.systemTemplateIdToReplace = systemTemplateIdToReplace;
    }

    @Override
    public String toString() {
        return "CopyTemplatesRequest{" +
                "fromEnvironment='" + fromEnvironment + '\'' +
                ", toEnvironment='" + toEnvironment + '\'' +
                ", fromType='" + fromType + '\'' +
                ", toType='" + toType + '\'' +
                ", fromUsername='" + fromUsername + '\'' +
                ", toUsername='" + toUsername + '\'' +
                ", templateIds=" + Arrays.toString(templateIds) +
                ", createNewSystemTemplate=" + createNewSystemTemplate +
                ", systemTemplateIdToReplace=" + systemTemplateIdToReplace +
                '}';
    }
}
