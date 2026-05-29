package com.pge.kraken.cis.exceptions;

public class TechnicalException extends ApplicationException {

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
