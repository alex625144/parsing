package com.parsing.pdf.parsing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RectangleDetector {

    static final int THRESHOLD = 10;
    static final Double OFFSET = 5.0;
    static final double GORIZONTAL_LINE_LENGTH = 700;
    private final TableProcessor tableProcessor;
    Mat cdstP = null;
    Mat verticalLines = null;

    public int detectRectangles(String fileSource) {
        List<double[]> lines = findLinesWithOpenCV(fileSource, GORIZONTAL_LINE_LENGTH);
        List<Double> sortedPointsY = sortList(lines);
        List<Double> distinctPointsY = mergeGorizontalLines(sortedPointsY);
        HorizontalLineCoordinate pointsWidthTable = findPointsWidthTable(lines);
        List<HorizontalLineCoordinate> sortedHorizontalLinesCoordinates = formHorizontalLinesCoordinates(distinctPointsY, pointsWidthTable);
        List<double[]> linesY = findLinesVerticalWithOpenCV("destination.png", 100);
        List<Double> sortedPointsX = sortListX(linesY);
        List<Double> distinctPointsX = mergeGorizontalLines(sortedPointsX);
        saveIMageWithVerticalLines(linesY);
        List<VerticalLineCoordinate> sortedVerticalLinesCoordinates = formVerticalLinesCoordinates(distinctPointsX, linesY);
        saveAllLines(sortedVerticalLinesCoordinates,sortedHorizontalLinesCoordinates);
        List<Rect> rects = tableProcessor.cropAllRowsRectangles(sortedHorizontalLinesCoordinates, sortedVerticalLinesCoordinates);
        createRectsImages(rects);
        return rects.size();
    }

    private List<VerticalLineCoordinate> formVerticalLinesCoordinates(List<Double> distinctPointsX, List<double[]> linesV) {
        List<VerticalLineCoordinate> result = new ArrayList<>();
        double max = 0;
        double min = 50;
        for (double[] line : linesV) {
            if (max < line[1]) {
                max = line[1];
            }
            if (min > line[3]) {
                min = line[3];
            }
        }
        for (int i = 0; i < distinctPointsX.size(); i++) {
            List<double[]> tempResult = new ArrayList<>();
            for (double[] coordinates : linesV) {
                if (isEqualsWithThreshold(distinctPointsX.get(i), coordinates)) {
                    tempResult.add(linesV.get(i));
                }
            }
            if (tempResult != null) {
                double[] sortedPointsY = sortListArray(tempResult);
                if (i == 0 || i == distinctPointsX.size() - 1) {
                    result.add(new VerticalLineCoordinate(max, min, distinctPointsX.get(i)));
                } else {
                    result.add(new VerticalLineCoordinate(max, sortedPointsY[3], distinctPointsX.get(i)));
                }
            }
        }
        return result;
    }

    private boolean isEqualsWithThreshold(Double distinctPointX, double[] coordinates) {
        return (Math.abs(coordinates[0]) - distinctPointX) < THRESHOLD;
    }

    private List<HorizontalLineCoordinate> formHorizontalLinesCoordinates(List<Double> lines, HorizontalLineCoordinate horizontalLineCoordinate) {
        List<HorizontalLineCoordinate> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            result.add(new HorizontalLineCoordinate(horizontalLineCoordinate.getLeftPoint(), horizontalLineCoordinate.getRightPoint(), lines.get(i)));
        }
        return result;
    }

    private void createRectsImages(List<Rect> rects) {
        final Mat source = Imgcodecs.imread("destination.png", Imgcodecs.IMREAD_GRAYSCALE);
        Mat cropRect = new Mat();
        for (int i = 0; i < rects.size(); i++) {
            try {
                cropRect = source.submat(rects.get(i));
            } catch (Exception ex) {
                log.warn("can't crop image");
            }
            Imgcodecs.imwrite("rect" + i + ".png", cropRect);
        }
    }

    private void saveIMageWithVerticalLines(List<double[]> linesV) {
        for (double[] point : linesV) {
            Imgproc.line(verticalLines, new Point(point[0], point[1]), new Point(point[2], point[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
        }
        Imgcodecs.imwrite("rectVertical.png", verticalLines);
    }

    private void saveAllLines(List<VerticalLineCoordinate> verticalLine, List<HorizontalLineCoordinate> horizontalLines) {
       verticalLine.forEach(
               x ->   Imgproc.line(verticalLines, new Point(x.getXCoordinate(), x.getTopPoint()), new Point(x.getXCoordinate(), x.getBottomPoint()),
                       new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0));
        horizontalLines.forEach(
                x ->   Imgproc.line(verticalLines, new Point(x.getLeftPoint(), x.getYCoordinate()), new Point(x.getRightPoint(), x.getYCoordinate()),
                        new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0));

        Imgcodecs.imwrite("All_Lines.png", verticalLines);
    }

    private List<Double> mergeGorizontalLines(List<Double> pointsY) {
        log.info("Lines has rows at start= " + pointsY.size());
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
        log.info("Lines has rows at finished= " + result.size());
        return result;
    }

    private List<Double> sortList(List<double[]> lines) {
        List<Double> pointsY = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            pointsY.add(lines.get(i)[1]);
        }
        return pointsY.stream().sorted().toList();
    }

    private double[] sortListArray(List<double[]> lines) {
        double[] line = new double[4];
        double minY = 50;
        double maxY = 0;
        double averageX = 0;

        for (int i = 0; i < lines.size(); i++) {
            if (minY > lines.get(i)[1]){
                minY = lines.get(i)[1];
            } else if(minY >lines.get(i)[3]){
                minY = lines.get(i)[3];
            } else if(maxY < lines.get(i)[1]) {
                maxY = lines.get(i)[1];
            } else if (maxY < lines.get(i)[3]) {
                maxY = lines.get(i)[3];
            }
            averageX = (averageX +lines.get(i)[0])/2;
        }
        line[0] = averageX;
        line[1] = maxY;
        line[2] = averageX;
        line[3] = minY;
        return line;
    }

    private List<Double> sortListX(List<double[]> lines) {
        List<Double> pointsY = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            pointsY.add(lines.get(i)[0]);
        }
        return pointsY.stream().sorted().toList();
    }

    private HorizontalLineCoordinate findPointsWidthTable(List<double[]> lines) {
        double x1 = 100;
        double x2 = 0;
        for (int x = 0; x < lines.size(); x++) {
            double[] array = lines.get(x);
            if (array[0] < x1) {
                x1 = array[0];
            }
            if (array[2] > x2) {
                x2 = array[2];
            }
        }
        return new HorizontalLineCoordinate(x1, x2, 0);
    }

    private List<double[]> findLinesVerticalWithOpenCV(String fileSource, double minLineLength) {
        Mat dst = new Mat();
        Mat cdst = new Mat();
        OpenCV.loadLocally();
        Mat source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);
        if (source.empty()) {
            log.warn("Error opening image!");
            log.warn("Program Arguments: [image_name -- default " + fileSource + "] \n");
            System.exit(-1);
        }
        Imgproc.Canny(source, dst, 50, 200, 3, false);
        Imgcodecs.imwrite("afterCannyRect.png", dst);
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        verticalLines = cdst.clone();
        Mat linesP = new Mat();
        Imgproc.HoughLinesP(dst, linesP, 3, Math.PI, 200, minLineLength, 10);
        List<double[]> lines = new ArrayList<>();
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            lines.add(l);
        }
        return lines;
    }

    private List<double[]> findLinesWithOpenCV(String fileSource, double minLineLength) {
        Mat dst = new Mat();
        Mat cdst = new Mat();
        OpenCV.loadLocally();
        Mat source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);
        if (source.empty()) {
            log.warn("Error opening image!");
            log.warn("Program Arguments: [image_name -- default " + fileSource + "] \n");
            System.exit(-1);
        }
        Imgproc.Canny(source, dst, 50, 200, 3, false);
        Imgcodecs.imwrite("afterCannyRect.png", dst);
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        cdstP = cdst.clone();
        Mat linesP = new Mat();
        Imgproc.HoughLinesP(dst, linesP, 3, Math.PI / 2, 5, minLineLength, 5);
        List<double[]> lines = new ArrayList<>();
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            lines.add(l);
        }
        return lines;
    }
}
