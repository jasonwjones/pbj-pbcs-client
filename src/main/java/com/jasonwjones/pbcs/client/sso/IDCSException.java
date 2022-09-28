package com.jasonwjones.pbcs.client.sso;

public class IDCSException extends RuntimeException {

    public IDCSException(String message, Throwable cause) {
        super(message, cause);
    }

    public IDCSException(Throwable cause) {
        super(cause);
    }

}