package com.parsing.parsers.pdf.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HorizontalLineCoordinate {

    private double leftPoint;
    private double rightPoint;
    private double yCoordinate;
}

