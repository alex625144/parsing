package com.parsing.pdf.parsing.modelParsing;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.opencv.core.Rect;

@Data
@AllArgsConstructor
public class Column {

    private Rect rect;
    private String parsingResult;
}
