package com.parsing.pdf.parsing;

import com.parsing.pdf.parsing.modelParsing.Column;
import com.parsing.pdf.parsing.modelParsing.Row;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Rect;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TableProcessor {

    private static Rect createRect(double xLeftBottom, double yLeftBottom, double xRightBottom, double yRightBottom, double yRightTop) {
        double width = Math.sqrt(Math.pow((xRightBottom - xLeftBottom), 2) + Math.pow((yRightBottom - yLeftBottom), 2));
        double height = Math.sqrt(Math.pow((yRightTop - yRightBottom), 2));
        return new Rect((int) xLeftBottom, (int) yLeftBottom, (int) width, (int) height);
    }

    private static Rectangle createRectangle(double xLeftBottom, double yLeftBottom, double xRightBottom, double yRightBottom, double yRightTop) {
        double width = Math.sqrt(Math.pow((xRightBottom - xLeftBottom), 2) + Math.pow((yRightBottom - yLeftBottom), 2));
        double height = Math.sqrt(Math.pow((yRightTop - yRightBottom), 2));
        return new Rectangle((int) xLeftBottom, (int) yLeftBottom, (int) width, (int) height);
    }

    public List<Rectangle> cropRectangles(List<HorizontalLineCoordinate> listHorizontal, List<VerticalLineCoordinate> listVertical) {
        List<Rectangle> listRectangle = new ArrayList<>();
        List<Row> table = new ArrayList<>();
        for (int top = 0, bottom = 1; bottom < listHorizontal.size(); top++, bottom++) {
            List<Column> row = new ArrayList<>();
            List<Rect> temp = new ArrayList<>();

            for (int mainLine = 0, borderLine = 1; borderLine < listVertical.size(); ) {
                if ( listVertical.get(borderLine).getBottomPoint() < listHorizontal.get(bottom).getYCoordinate()) {
                    borderLine++;
                    continue;
                }
                double x1 = listVertical.get(mainLine).getXCoordinate();
                double y1 = listHorizontal.get(top).getYCoordinate();
                double x2 = listVertical.get(borderLine).getXCoordinate();
                double y2 = y1;
                double height = listHorizontal.get(bottom).getYCoordinate();
                listRectangle.add(createRectangle(x1,y1,x2,y2,height));
                temp.add(createRect(x1, y1, x2, y2, height));
                mainLine = borderLine++;
                row = temp.stream().map(x-> new Column(x, null)).toList();
            }
            table.add(new Row(row));
        }
        return listRectangle;
    }
}
