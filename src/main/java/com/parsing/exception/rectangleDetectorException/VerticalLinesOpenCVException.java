package com.parsing.exception.rectangleDetectorException;

public class VerticalLinesOpenCVException extends RuntimeException{
    public VerticalLinesOpenCVException(Throwable cause) {
        super("Method \"RectangleDetector.findVerticalLinesWithOpenCV\" fail" ,cause);
    }
}
