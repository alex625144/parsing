package com.parsing.exception;

public class RecognizeLotPDFResultException extends RuntimeException{
    public RecognizeLotPDFResultException(Throwable cause) {
        super("Can't recognize LotPDF result in method: \"LotPDFResultService.recognizeLotPDFResult\"", cause);
    }
}
