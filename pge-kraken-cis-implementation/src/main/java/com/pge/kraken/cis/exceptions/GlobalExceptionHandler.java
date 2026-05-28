package com.pge.kraken.cis.exceptions;

public class GlobalExceptionHandler {

    public String format(Exception exception) {
        return exception.getClass().getSimpleName() + ": " + exception.getMessage();
    }
}
