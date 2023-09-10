package com.parsing.exception;

public class TableDetectorException extends RuntimeException {
    public TableDetectorException(Throwable cause) {
        super("Method \"TableDetector.detectQuantityOfTables\" fail", cause);
    }
}
