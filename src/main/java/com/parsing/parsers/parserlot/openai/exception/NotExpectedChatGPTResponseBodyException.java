package com.parsing.parsers.parserlot.openai.exception;

public class NotExpectedChatGPTResponseBodyException extends RuntimeException {

    private static final String MASSAGE = "Not expected ChatGPT response body";

    public NotExpectedChatGPTResponseBodyException(Throwable cause) {
        super(MASSAGE, cause);
    }
}
