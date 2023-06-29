package com.parsing.parsers.pdf.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerticalLineCoordinate {

    private double topPoint;
    private double bottomPoint;
    private double xCoordinate;
}

