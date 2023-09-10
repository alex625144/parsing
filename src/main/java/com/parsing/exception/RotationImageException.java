package com.parsing.exception;

public class RotationImageException extends RuntimeException{
    public RotationImageException(Throwable cause) {
        super("Method \"RotationImage.rotate\" fail." ,cause);
    }
}
