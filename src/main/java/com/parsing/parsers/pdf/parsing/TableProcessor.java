package com.parsing.parsers.pdf.parsing;

import com.parsing.exception.TableProcessorException;
import com.parsing.parsers.pdf.parsing.model.Column;
import com.parsing.parsers.pdf.parsing.model.HorizontalLineCoordinate;
import com.parsing.parsers.pdf.parsing.model.Row;
import com.parsing.parsers.pdf.parsing.model.VerticalLineCoordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableProcessor {

    public Rectangle createRectangle(double xLeftBottom, double yLeftBottom, double xRightBottom, double yRightBottom, double yRightTop) {
        log.debug("Class TableProcessor.createRectangle started.");
        try {
            double width = Math.sqrt(Math.pow((xRightBottom - xLeftBottom), 2) + Math.pow((yRightBottom - yLeftBottom), 2));
            double height = Math.sqrt(Math.pow((yRightTop - yRightBottom), 2));
            log.debug("Class TableProcessor.createRectangle finished.");
            return new Rectangle((int) xLeftBottom, (int) yLeftBottom, (int) width, (int) height);
        } catch (Exception e) {
            throw new TableProcessorException("Create rectangle failed.", e);
        }
    }

    public List<Row> cropRectangles(List<HorizontalLineCoordinate> horizontalLines, List<VerticalLineCoordinate> verticalLines) {
        log.debug("Class TableProcessor.cropRectangles started.");
        List<Row> table = new ArrayList<>();
        try {
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
            log.debug("Class TableProcessor.cropRectangles finished.");
            return table;
        } catch (Exception e) {
            throw new TableProcessorException("Crop rectangles failed.", e);
        }
    }
}
