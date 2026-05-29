package com.pge.kraken.cis.exceptions;

public class ValidationException extends ApplicationException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
