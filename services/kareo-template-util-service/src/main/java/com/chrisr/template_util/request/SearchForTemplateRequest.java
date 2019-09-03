package com.chrisr.template_util.request;

public class SearchForTemplateRequest {

    private String title;
    private String findPartialTitleMatches;
    private String type;
    private String author;
    private String version;
    private String username;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFindPartialTitleMatches() {
        return findPartialTitleMatches;
    }

    public void setFindPartialTitleMatches(String findPartialTitleMatches) {
        this.findPartialTitleMatches = findPartialTitleMatches;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "SearchForTemplateRequest{" +
                "title='" + title + '\'' +
                ", findPartialTitleMatches='" + findPartialTitleMatches + '\'' +
                ", type='" + type + '\'' +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
