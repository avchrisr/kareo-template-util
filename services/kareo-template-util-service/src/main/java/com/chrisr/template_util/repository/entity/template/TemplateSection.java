package com.chrisr.template_util.repository.entity.template;

public class TemplateSection {

    private String sectionMetaData;
    private String inherit;
    private String key;

    public String getSectionMetaData() {
        return sectionMetaData;
    }

    public void setSectionMetaData(String sectionMetaData) {
        this.sectionMetaData = sectionMetaData;
    }

    public String getInherit() {
        return inherit;
    }

    public void setInherit(String inherit) {
        this.inherit = inherit;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "TemplateSection{" +
                "sectionMetaData='" + sectionMetaData + '\'' +
                ", inherit='" + inherit + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
