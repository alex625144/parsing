package com.parsing.exception.tableRecognizerException;

public class TableExistException extends RuntimeException {
    public TableExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
