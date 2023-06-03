package com.parsing.parsers.pdf.parsing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ManyTableDetector {

    private static final Double OFFSET = 5.0;

    public int manyTableDetector(List<double[]> lines) {
        final List<double[]> sortLinesByY = sortLinesByY(lines);
        final List<Double> sortedY = sortY(lines);
        final List<Double> mergedY = mergeY(sortedY);
        final List<double[]> mergedLines = mergeLinesByY(sortLinesByY, mergedY);
        final int lineCrossover = groupVerticalLines(mergedLines, mergedY);
        return lineCrossover;
    }

    private int groupVerticalLines(List<double[]> lines, List<Double> mergedY) {
        int lineCrossover = 0;
        List<Double> yUpList = getListCoordinate(lines, 1);
        List<Double> yDownList = getListCoordinate(lines, 3);
        for (Double yUp : yUpList) {
            for (Double yDown : yDownList) {
                if (yDown > yUp) {
                    lineCrossover++;
                }
            }
        }
        log.debug(String.valueOf("Line conversiont" + lineCrossover));
        return lineCrossover;
    }

    private List<Double> getListCoordinate(List<double[]> lines, int coordinate) {
        List<Double> result = new ArrayList<>();
        for (double[] line : lines) {
            result.add(line[coordinate]);
        }
        return result;
    }

    private List<Double> mergeY(List<Double> pointsY) {
        log.debug("Lines has rows at start= " + pointsY.size());
        List<Double> result = new ArrayList<>(pointsY);
        for (int i = 0; i < pointsY.size(); i++) {
            if (i + 1 < pointsY.size()) {
                double point1 = pointsY.get(i);
                double point2 = pointsY.get(i + 1);
                double diff = point2 - point1;
                if (diff < OFFSET) {
                    result.remove(point2);
                }
            }
        }
        log.debug("Lines has rows at finished= " + result.size());
        return result;
    }

    private List<double[]> mergeLinesByY(List<double[]> lines, List<Double> mergedY) {
        List<double[]> result = new ArrayList<>();
        for (double[] line : lines) {
            for (Double yCoordinateLine : mergedY) {
                if (line[1] == yCoordinateLine) {
                    result.add(line);
                }
            }
        }
        return result;
    }

    private List<double[]> sortLinesByY(List<double[]> lines) {
        List<double[]> sortedArrayY = new ArrayList<>();
        List<Double> listY = new ArrayList<>();
        for (double[] line : lines) {
            listY.add(line[1]);
        }
        final List<Double> listYSorted = listY.stream().sorted().toList();
        for (Double yCoordinate : listYSorted) {
            for (double[] line : lines) {
                if (yCoordinate == line[1]) {
                    sortedArrayY.add(line);
                }
            }
        }
        return sortedArrayY;
    }

    private List<Double> sortY(List<double[]> lines) {
        List<Double> pointsY = new ArrayList<>();
        for (double[] line : lines) {
            pointsY.add(line[1]);
        }
        return pointsY.stream().sorted().toList();
    }
}