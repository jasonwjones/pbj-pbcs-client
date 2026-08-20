package com.jasonwjones.pbcs.client.exceptions;

/**
 * Thrown when the PBCS REST API rejects the supplied credentials.
 */
public class PbcsInvalidCredentialsException extends PbcsClientException {

    /**
     * Constructs an instance with the given message.
     *
     * @param message the detail message
     */
    public PbcsInvalidCredentialsException(String message) {
        super(message);
    }

}
