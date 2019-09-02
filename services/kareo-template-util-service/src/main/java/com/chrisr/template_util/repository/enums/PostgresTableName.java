package com.chrisr.template_util.repository.enums;

public enum PostgresTableName {

    REQUEST_HISTORY("request_history");

    private String name;

    PostgresTableName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
