package com.jasonwjones.pbcs.client.sso;

/**
 * Thrown when an IDCS/OCI IAM authentication flow fails, such as an invalid or expired token exchange.
 */
public class IDCSException extends RuntimeException {

    /**
     * Constructs an instance with the given message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public IDCSException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an instance with the given cause.
     *
     * @param cause the cause
     */
    public IDCSException(Throwable cause) {
        super(cause);
    }

}
