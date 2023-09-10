package com.parsing.exception;

public class ManyTableDetector extends RuntimeException {
    public ManyTableDetector(Throwable cause) {
        super("Method \"ManyTableDetector.detectQuantityOfTables\" fail: ", cause);
    }
}
