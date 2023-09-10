package com.parsing.exception;

public class CropImageException extends RuntimeException{
    public CropImageException(Throwable cause) {
        super("method \"CropImage.cropImage\" fail. Crop image fail: ", cause);
    }

}
