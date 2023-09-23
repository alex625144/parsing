package com.parsing.exception;

public class UnableToConvertPriceException extends RuntimeException {

    public UnableToConvertPriceException(String message, Throwable cause) {
        super(message, cause);
    }
}
