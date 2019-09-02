package com.chrisr.template_util.repository.entity.template;

public class TemplateCarePlan {

    private String carePlanMetaData;
    private String inherit;
    private String key;

    public String getCarePlanMetaData() {
        return carePlanMetaData;
    }

    public void setCarePlanMetaData(String carePlanMetaData) {
        this.carePlanMetaData = carePlanMetaData;
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
        return "TemplateCarePlan{" +
                "carePlanMetaData='" + carePlanMetaData + '\'' +
                ", inherit='" + inherit + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
