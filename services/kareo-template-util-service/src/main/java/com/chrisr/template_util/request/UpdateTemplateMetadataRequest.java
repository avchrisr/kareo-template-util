package com.chrisr.template_util.request;

import javax.validation.constraints.NotNull;

public class UpdateTemplateMetadataRequest {

	@NotNull
	private Long existingTemplateId;

	private String newTitle;
	private String newAuthor;
	private String newVersion;

	public Long getExistingTemplateId() {
		return existingTemplateId;
	}

	public void setExistingTemplateId(Long existingTemplateId) {
		this.existingTemplateId = existingTemplateId;
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
				"existingTemplateId=" + existingTemplateId +
				", newTitle='" + newTitle + '\'' +
				", newAuthor='" + newAuthor + '\'' +
				", newVersion='" + newVersion + '\'' +
				'}';
	}
}
