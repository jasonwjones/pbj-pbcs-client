package com.jasonwjones.pbcs.aif;

import java.util.StringJoiner;

public class AifAppProperty {

    private Integer status;

    private String details;

    private String propertyName;

    private String propertyScope;

    private String propertyValue;

    private Integer propertyValueId;

    // there are more, but they are all null for me right now so not sure if string/integer

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyScope() {
        return propertyScope;
    }

    public void setPropertyScope(String propertyScope) {
        this.propertyScope = propertyScope;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Integer getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(Integer propertyValueId) {
        this.propertyValueId = propertyValueId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AifAppProperty.class.getSimpleName() + "[", "]")
                .add("propertyName='" + propertyName + "'")
                .add("propertyValue='" + propertyValue + "'")
                .toString();
    }

}