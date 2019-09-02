package com.chrisr.template_util.repository.entity.template;

import java.time.LocalDateTime;

public abstract class BaseRequest {

    private long id;
    private String requestType;
    private String username;
    private String datetime;

    BaseRequest(String requestType, String username) {
        this.requestType = requestType;
        this.username = username;
        this.datetime = LocalDateTime.now().toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "BaseRequest{" +
                "id=" + id +
                ", requestType='" + requestType + '\'' +
                ", username='" + username + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
