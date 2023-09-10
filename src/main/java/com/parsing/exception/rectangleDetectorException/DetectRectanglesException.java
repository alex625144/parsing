package com.parsing.exception.rectangleDetectorException;

public class DetectRectanglesException extends RuntimeException{
    public DetectRectanglesException(Throwable cause) {
        super("Method \"RectangleDetector.detectRectangles\" fail." ,cause);
    }
}
