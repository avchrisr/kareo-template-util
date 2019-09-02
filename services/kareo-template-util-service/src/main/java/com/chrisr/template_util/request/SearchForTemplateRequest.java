package com.chrisr.template_util.request;

public class SearchForTemplateRequest {

    private String title;
    private String type;
    private String username;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
                ", type='" + type + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
