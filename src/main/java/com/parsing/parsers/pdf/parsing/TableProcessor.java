package com.parsing.parsers.pdf.parsing;

import com.parsing.parsers.pdf.parsing.model.Column;
import com.parsing.parsers.pdf.parsing.model.HorizontalLineCoordinate;
import com.parsing.parsers.pdf.parsing.model.Row;
import com.parsing.parsers.pdf.parsing.model.VerticalLineCoordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TableProcessor {

    public Rectangle createRectangle(double xLeftBottom, double yLeftBottom, double xRightBottom, double yRightBottom, double yRightTop) {
        double width = Math.sqrt(Math.pow((xRightBottom - xLeftBottom), 2) + Math.pow((yRightBottom - yLeftBottom), 2));
        double height = Math.sqrt(Math.pow((yRightTop - yRightBottom), 2));
        return new Rectangle((int) xLeftBottom, (int) yLeftBottom, (int) width, (int) height);
    }

    public List<Row> cropRectangles(List<HorizontalLineCoordinate> horizontalLines, List<VerticalLineCoordinate> verticalLines) {
        List<Row> table = new ArrayList<>();
        for (int top = 0, bottom = 1; bottom < horizontalLines.size(); top++, bottom++) {
            List<Column> row = new ArrayList<>();
            List<Rectangle> temp = new ArrayList<>();
            for (int mainLine = 0, borderLine = 1; borderLine < verticalLines.size(); ) {
                if (verticalLines.get(borderLine).getBottomPoint() < horizontalLines.get(bottom).getYCoordinate()) {
                    borderLine++;
                } else {
                    double x1 = verticalLines.get(mainLine).getXCoordinate();
                    double y1 = horizontalLines.get(top).getYCoordinate();
                    double x2 = verticalLines.get(borderLine).getXCoordinate();
                    double y2 = y1;
                    double height = horizontalLines.get(bottom).getYCoordinate();
                    temp.add(createRectangle(x1, y1, x2, y2, height));
                    mainLine = borderLine++;
                    row = temp.stream().map(x -> new Column(x, null)).toList();
                }
            }
            table.add(new Row(row));
        }
        return table;
    }
}
