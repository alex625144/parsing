package com.parsing.exception.parserPDFException;

public class ParseProzorroFileForSchedulerException extends RuntimeException {
    public ParseProzorroFileForSchedulerException(Throwable cause) {
        super("Method \"ParserPDF.parseProzorroFileForScheduler\" fail", cause);
    }
}
