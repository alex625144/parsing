package com.parsing.exception;

public class RotationImageException extends RuntimeException {
    public RotationImageException(String message, Throwable cause) {
        super(String.format("Method \"RotationImage.rotate\" fail. %s", message), cause);
    }
}
