package com.parsing.pdf.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HorizontalLineCoordinate {

    private double leftPoint;
    private double rightPoint;
    private double yCoordinate;
}

