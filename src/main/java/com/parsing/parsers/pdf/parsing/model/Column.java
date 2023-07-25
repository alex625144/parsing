package com.parsing.parsers.pdf.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.Rectangle;

@Getter
@Setter
@AllArgsConstructor
public class Column {

    private Rectangle rectangle;
    private String parsingResult;
}
