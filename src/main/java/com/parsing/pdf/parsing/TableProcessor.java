package com.parsing.pdf.parsing;

import org.opencv.core.Rect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TableProcessor {

    private static Rect createRect(double xLeftBottom, double yLeftBottom, double xRightBottom, double yRightBottom, double yRightTop) {
        double width = Math.sqrt(Math.pow((xRightBottom - xLeftBottom), 2) + Math.pow((yRightBottom - yLeftBottom), 2));
        double height = Math.sqrt(Math.pow((yRightTop - yRightBottom), 2));
        return new Rect((int) xLeftBottom, (int) yRightTop, (int) width, (int) height);
    }

    public List<Rect> cropRowsRectangles(List<Double> distinctPointsY, HorizontalLineCoordinate horizontalLineCoordinate) {
        List<Rect> result = new ArrayList<>();
        Collections.reverse(distinctPointsY);
        for (int i = 0; i < distinctPointsY.size(); i++) {
            if (i + 1 < distinctPointsY.size()) {
                result.add(createRect(horizontalLineCoordinate.getLeftPoint(), distinctPointsY.get(i),
                        horizontalLineCoordinate.getRightPoint(), distinctPointsY.get(i),
                        distinctPointsY.get(i + 1)));
            }
        }
        return result;
    }

    public List<Rect> cropAllRowsRectangles(List<HorizontalLineCoordinate> listHorizontal, List<VerticalLineCoordinate> listVertical) {
        List<Rect> result = new ArrayList<>();
        Collections.reverse(listHorizontal);
        if (listHorizontal.size() > 1 || listVertical.size() > 1) {
            for (int i = 1; i < listHorizontal.size(); i++) {
                for (int j = 1, k = 0; j < listVertical.size();) {
                    if (listHorizontal.get(i).getYCoordinate()+5 >= listVertical.get(j).getBottomPoint()) {
                        result.add(createRect(listVertical.get(k).getXCoordinate(), listHorizontal.get(i).getYCoordinate(),
                                listVertical.get(j).getXCoordinate(), listHorizontal.get(i).getYCoordinate(),
                                listHorizontal.get(i - 1).getYCoordinate()));
                        k++;
                    }
                    j++;
                }
            }
        }
        return result;
    }
}
