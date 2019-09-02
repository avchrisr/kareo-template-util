package com.chrisr.template_util.repository.enums;

public enum OracleTableName {

    TEMPLATES("TEMPLATE"),
    TEMPLATE_SECTIONS("SECTION"),
    TEMPLATE_CAREPLANS("CAREPLAN"),
    TEMPLATE_SPECIALTIES("SPECIALTY");

    private String tableAcronym;

    OracleTableName(String tableAcronym) {
        this.tableAcronym = tableAcronym;
    }

    public String getTableAcronym() {
        return tableAcronym;
    }
}
