package com.parsing.parsers.pdf.parsing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ManyTableDetector {

    private static final Double OFFSET = 5.0;

    public List<double[]> detectQuantityOfTables(List<double[]> lines) {
        final List<double[]> sortedLines = sortLinesByX(lines);
        for (double[] line : sortedLines) {
            log.debug(Arrays.toString(line));
        }
        log.debug("////////////////////////////");
        final List<double[]> mergedLines1 = newMergeLines(sortedLines);
        final List<double[]> mergedLines2 = newMergeLines(mergedLines1);
        final List<double[]> mergedLines3 = newMergeLines(mergedLines2);
        for (double[] line : mergedLines3) {
            log.debug(Arrays.toString(line));
        }
        return mergedLines3;
    }

    private List<double[]> newMergeLines(List<double[]> lines) {
        List<double[]> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (i + 1 < lines.size()) {
                if ((lines.get(i + 1)[0] - lines.get(i)[0]) < OFFSET) {
                    if (lines.get(i + 1)[1] > lines.get(i)[3]) {
                        double x1Coordinate = lines.get(i)[0];
                        double x2Coordinate = lines.get(i)[2];
                        double y1Coordinate = Math.max(lines.get(i + 1)[1], lines.get(i)[1]);
                        double y2Coordinate = Math.min(lines.get(i + 1)[3], lines.get(i)[3]);
                        double[] temp = {x1Coordinate, y1Coordinate, x2Coordinate, y2Coordinate};
                        result.add(temp);
                    }
                    i++;
                } else {
                    result.add(lines.get(i));
                }
            }
        }
        return result;
    }

    private int groupVerticalLines(List<double[]> lines, List<Double> mergedY) {
        int lineCrossover = 0;
        List<Double> yUpList = getListCoordinate(lines, 1);
        List<Double> yDownList = getListCoordinate(lines, 3);
        for (double[] line : lines) {
            log.debug(Arrays.toString(line));
        }
        for (Double yUp : yUpList) {
            for (Double yDown : yDownList) {
                if (yDown < yUp) {
                    log.debug("Ydown " + yDown + " Yup" + yUp);
                    lineCrossover++;
                }
            }
        }
        log.debug("Line conversion = " + lineCrossover);
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

    private List<double[]> sortLinesByX(List<double[]> lines) {
        List<double[]> sortedArrayX = new ArrayList<>();
        List<Double> listX = new ArrayList<>();
        for (double[] line : lines) {
            listX.add(line[0]);
        }
        final List<Double> listXSorted = listX.stream().sorted().toList();
        log.debug(listXSorted.toString());
        for (Double xCoordinate : listXSorted) {
            for (double[] line : lines) {
                if (xCoordinate == line[0]) {
                    sortedArrayX.add(line);
                }
            }
        }
        return sortedArrayX;
    }

    private List<Double> getSortX(List<double[]> lines) {
        List<Double> pointsY = new ArrayList<>();
        for (double[] line : lines) {
            pointsY.add(line[0]);
        }
        return pointsY.stream().sorted().toList();
    }
}