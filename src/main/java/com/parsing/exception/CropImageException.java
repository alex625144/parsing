package com.parsing.exception;

public class CropImageException extends RuntimeException {
    public CropImageException(String message, Throwable cause) {
        super(String.format("method \"CropImage.cropImage\" fail. %s ", message), cause);
    }
}
