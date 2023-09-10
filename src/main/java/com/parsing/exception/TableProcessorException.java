package com.parsing.exception;

public class TableProcessorException extends RuntimeException{
    public TableProcessorException(Throwable cause) {
        super("Create/crop rectangles fail."  ,cause);
    }
}
