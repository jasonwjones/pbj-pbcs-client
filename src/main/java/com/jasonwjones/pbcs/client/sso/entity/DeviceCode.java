package com.jasonwjones.pbcs.client.sso.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps the response returned by IDCS/OCI IAM's device authorization endpoint when starting the OAuth
 * device code flow.
 */
public class DeviceCode {

    @JsonProperty("device_code")
    private String deviceCode;

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("verification_uri")
    private String verificationUri;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * Constructs an empty instance for deserialization.
     */
    public DeviceCode() {
    }

    /**
     * Gets the device code, used to poll the token endpoint until the user completes authorization.
     *
     * @return the device code
     */
    public String getDeviceCode() {
        return deviceCode;
    }

    /**
     * Sets the device code.
     *
     * @param deviceCode the device code
     */
    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    /**
     * Gets the user code that the user must enter at the verification URI to authorize the device.
     *
     * @return the user code
     */
    public String getUserCode() {
        return userCode;
    }

    /**
     * Sets the user code.
     *
     * @param userCode the user code
     */
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    /**
     * Gets the URI the user should visit to enter the user code and authorize the device.
     *
     * @return the verification URI
     */
    public String getVerificationUri() {
        return verificationUri;
    }

    /**
     * Sets the verification URI.
     *
     * @param verificationUri the verification URI
     */
    public void setVerificationUri(String verificationUri) {
        this.verificationUri = verificationUri;
    }

    /**
     * Gets the number of seconds until the device code expires.
     *
     * @return the expiration time, in seconds from issuance
     */
    public Integer getExpiresIn() {
        return expiresIn;
    }

    /**
     * Sets the number of seconds until the device code expires.
     *
     * @param expiresIn the expiration time, in seconds from issuance
     */
    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

}
