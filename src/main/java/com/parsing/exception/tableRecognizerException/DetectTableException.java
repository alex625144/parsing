package com.parsing.exception.tableRecognizerException;

public class DetectTableException extends RuntimeException {
    public DetectTableException(Throwable cause) {
        super("Table not detected", cause);
    }
}
