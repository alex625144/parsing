package com.parsing.pdf.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

@Data
@AllArgsConstructor
public class Column {

    private Rectangle rectangle;
    private String parsingResult;
}
