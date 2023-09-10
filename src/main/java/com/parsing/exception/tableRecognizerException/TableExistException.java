package com.parsing.exception.tableRecognizerException;

public class TableExistException extends RuntimeException{
    public TableExistException(Throwable cause) {
        super("Table doesn't exist on page" ,cause);
    }
}
