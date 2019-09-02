package com.chrisr.template_util.repository.entity.template;

public class Template {

	private long id;
	private String type;
	private String title;
	private String author;
	private String version;
	private String username;
	private String createdOn;
	private String updatedOn;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	@Override
	public String toString() {
		return "Template{" +
				"id=" + id +
				", type='" + type + '\'' +
				", title='" + title + '\'' +
				", author='" + author + '\'' +
				", version='" + version + '\'' +
				", username='" + username + '\'' +
				", createdOn='" + createdOn + '\'' +
				", updatedOn='" + updatedOn + '\'' +
				'}';
	}
}
