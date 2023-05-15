package com.parsing.pdf.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerticalLineCoordinate {

    private double topPoint;
    private double bottomPoint;
    private double xCoordinate;
}

