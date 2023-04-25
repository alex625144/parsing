package com.parsing.pdf.parsing;

import org.opencv.core.Rect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TableProcessor {

    private static Rect createRect(double x1, double y1, double x2, double y2, double y3) {
        double width = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        double height = Math.sqrt(Math.pow((y3 - y2), 2));
        return new Rect((int) x1, (int) y3, (int) width, (int) height);
    }

    public List<Rect> cropRowsRectangles(List<Double> distinctPointsY, TableWidth tableWidth) {
        List<Rect> result = new ArrayList<>();
        Collections.reverse(distinctPointsY);
        for (int i = 0; i < distinctPointsY.size(); i++) {
            if (i + 1 < distinctPointsY.size()) {
                result.add(createRect(tableWidth.getLeftPoint(), distinctPointsY.get(i),
                        tableWidth.getRightPoint(), distinctPointsY.get(i),
                        distinctPointsY.get(i + 1)));
            }
        }
        return result;
    }
}
