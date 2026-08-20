package com.jasonwjones.pbcs.aif;

import java.util.StringJoiner;

/**
 * Maps a single application property as returned alongside application details by the unofficial data
 * management (DM/AIF) "applications" endpoint.
 */
public class AifAppProperty {

    private Integer status;

    private String details;

    private String propertyName;

    private String propertyScope;

    private String propertyValue;

    private Integer propertyValueId;

    // there are more, but they are all null for me right now so not sure if string/integer

    /**
     * Constructs an empty instance for deserialization.
     */
    public AifAppProperty() {
    }

    /**
     * Gets the status code for this property.
     *
     * @return the status code
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the status code for this property.
     *
     * @param status the status code
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Gets any additional details for this property.
     *
     * @return the details, may be null
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets additional details for this property.
     *
     * @param details the details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Gets the name of this property.
     *
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the name of this property.
     *
     * @param propertyName the property name
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Gets the scope of this property (e.g., which application/plan the property applies to).
     *
     * @return the property scope
     */
    public String getPropertyScope() {
        return propertyScope;
    }

    /**
     * Sets the scope of this property.
     *
     * @param propertyScope the property scope
     */
    public void setPropertyScope(String propertyScope) {
        this.propertyScope = propertyScope;
    }

    /**
     * Gets the value of this property.
     *
     * @return the property value
     */
    public String getPropertyValue() {
        return propertyValue;
    }

    /**
     * Sets the value of this property.
     *
     * @param propertyValue the property value
     */
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * Gets the identifier for this property's value.
     *
     * @return the property value ID
     */
    public Integer getPropertyValueId() {
        return propertyValueId;
    }

    /**
     * Sets the identifier for this property's value.
     *
     * @param propertyValueId the property value ID
     */
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
