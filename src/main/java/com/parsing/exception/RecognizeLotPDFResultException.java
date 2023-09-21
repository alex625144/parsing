package com.parsing.exception;

public class RecognizeLotPDFResultException extends RuntimeException {
    public RecognizeLotPDFResultException(String message, Throwable cause) {
        super(String.format("Method: \"LotPDFResultService.recognizeLotPDFResult\" faild. %s", message), cause);
    }
}
