package com.chrisr.template_util.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UpdateTemplateMetadataRequest {

    @NotBlank
    private String environment;

	@NotNull
	private long currentTemplateId;

	@NotBlank
	private String currentTemplateTitle;

	private String newTitle;
	private String newAuthor;
	private String newVersion;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

	public long getCurrentTemplateId() {
		return currentTemplateId;
	}

	public void setCurrentTemplateId(long currentTemplateId) {
		this.currentTemplateId = currentTemplateId;
	}

	public String getCurrentTemplateTitle() {
		return currentTemplateTitle;
	}

	public void setCurrentTemplateTitle(String currentTemplateTitle) {
		this.currentTemplateTitle = currentTemplateTitle;
	}

	public String getNewTitle() {
		return newTitle;
	}

	public void setNewTitle(String newTitle) {
		this.newTitle = newTitle;
	}

	public String getNewAuthor() {
		return newAuthor;
	}

	public void setNewAuthor(String newAuthor) {
		this.newAuthor = newAuthor;
	}

	public String getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}

    @Override
    public String toString() {
        return "UpdateTemplateMetadataRequest{" +
                "environment='" + environment + '\'' +
                ", currentTemplateId=" + currentTemplateId +
                ", currentTemplateTitle='" + currentTemplateTitle + '\'' +
                ", newTitle='" + newTitle + '\'' +
                ", newAuthor='" + newAuthor + '\'' +
                ", newVersion='" + newVersion + '\'' +
                '}';
    }
}
