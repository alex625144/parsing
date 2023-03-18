package com.parsing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PDFParsingException extends RuntimeException{

    public PDFParsingException (String message) {
        super(message);
    }
}
