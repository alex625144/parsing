package com.parsing.exception.rectangleDetectorException;

public class ImageVerticalLinesException extends RuntimeException{
    public ImageVerticalLinesException(Throwable cause) {
        super("Method \"RectangleDetector.saveIMageWithVerticalLines\" fail." ,cause);
    }
}
