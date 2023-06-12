package com.parsing.parsers.pdf.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Table {

    private List<Row> row;
}
