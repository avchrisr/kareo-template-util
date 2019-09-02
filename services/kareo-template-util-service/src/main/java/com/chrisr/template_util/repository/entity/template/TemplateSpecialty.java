package com.chrisr.template_util.repository.entity.template;

public class TemplateSpecialty {

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TemplateSpecialty{" +
                "description='" + description + '\'' +
                '}';
    }
}
