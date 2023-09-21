package com.parsing.exception.parserPDFException;

public class ParseProzorroFileException extends RuntimeException {
    public ParseProzorroFileException(Throwable cause) {
        super("Method \"ParserPDF.parseProzorroFile\" fail: ", cause);
    }
}
