package com.parsing.pdf.parsing.modelParsing;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Row {

    private List<Column> columns;
}
