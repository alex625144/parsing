package com.parsing.parsers.pdf.parsing;

import com.parsing.exception.rectangleDetectorException.DetectRectanglesException;
import com.parsing.exception.rectangleDetectorException.ImageVerticalLinesException;
import com.parsing.exception.rectangleDetectorException.VerticalLinesOpenCVException;
import com.parsing.parsers.pdf.parsing.model.HorizontalLineCoordinate;
import com.parsing.parsers.pdf.parsing.model.Row;
import com.parsing.parsers.pdf.parsing.model.VerticalLineCoordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.parsing.Constants.X1;
import static com.parsing.Constants.X2;
import static com.parsing.Constants.Y1;
import static com.parsing.Constants.Y2;
import static com.parsing.Constants.PERCENT_HORIZONTAL_LINE_LENGTH;

@Slf4j
@Component
@RequiredArgsConstructor
public class RectangleDetector {

    static final Double OFFSET = 5.0;
    static final int HORIZONTAL_THRESHOLD = 5;
    static final double ALL_LINES_THRESHOLD1 = 50;
    static final double ALL_LINES_THRESHOLD2 = 200;
    static final int ALL_LINES_APERTURESIZE = 3;
    static final int ALL_LINES_RHO = 3;
    static final int ALL_LINES_MAXLINEGAP = 5;
    static final double VERTICAL_LINE_LENGTH = 100;
    private final TableProcessor tableProcessor;
    double maxCoordinate = 0;
    double minCoordinate = 50;
    Mat verticalLinesMat = null;

    public List<Row> detectRectangles(String fileSource, int pageNumber) {
        log.info("Class RectangleDetector started.");
        try {
            List<double[]> horizontalLines = findHorizontalLinesWithOpenCV(fileSource);
            if (horizontalLines.size() > 2) {
                List<Double> sortedHorizontalLines = sortLinesByY(horizontalLines);
                List<Double> mergedHorizontalLines = mergeLines(sortedHorizontalLines);
                HorizontalLineCoordinate horizontalLineCoordinate = findExtremeHorizontalTablePoints(horizontalLines);
                List<HorizontalLineCoordinate> sortedHorizontalLinesCoordinates = formHorizontalLinesCoordinates(mergedHorizontalLines, horizontalLineCoordinate);
                List<double[]> verticalLines = findVerticalLinesWithOpenCV(fileSource, 10);
                List<Double> sortedVerticalLines = sortLinesByX(verticalLines);
                List<Double> mergedVerticalLines = mergeLines(sortedVerticalLines);
                List<VerticalLineCoordinate> sortedVerticalLinesCoordinates = formVerticalLinesCoordinates(mergedVerticalLines, verticalLines);

                saveIMageWithVerticalLines(verticalLines, pageNumber);
                saveAllLines(sortedVerticalLinesCoordinates, sortedHorizontalLinesCoordinates);

                return tableProcessor.cropRectangles(sortedHorizontalLinesCoordinates, sortedVerticalLinesCoordinates);
            }
            log.info("Class RectangleDetector finished.");
            return new ArrayList<>();
        } catch (Exception e) {
            throw new DetectRectanglesException("Detecting rectangles failed.", e);
        }
    }

    private List<VerticalLineCoordinate> formVerticalLinesCoordinates(List<Double> distinctPointsX, List<double[]> linesV) {
        List<VerticalLineCoordinate> result = new ArrayList<>();
        getMinMaxCoordinates(linesV);
        for (int i = 0; i < distinctPointsX.size(); i++) {
            List<double[]> tempResult = new ArrayList<>();
            for (double[] coordinates : linesV) {
                if (isEqualsWithThreshold(distinctPointsX.get(i), coordinates)) {
                    tempResult.add(coordinates);
                }
            }
            if (!tempResult.isEmpty()) {
                double[] sortedPointsY = sortListArray(tempResult);
                if (i == 0 || i == distinctPointsX.size() - 1) {
                    result.add(new VerticalLineCoordinate(minCoordinate, maxCoordinate, distinctPointsX.get(i)));
                } else {
                    result.add(new VerticalLineCoordinate(minCoordinate, sortedPointsY[1], distinctPointsX.get(i)));
                }
            }
        }
        return result;
    }

    private void getMinMaxCoordinates(List<double[]> linesV) {
        for (double[] line : linesV) {
            if (maxCoordinate < line[1]) {
                maxCoordinate = line[1];
            }
            if (minCoordinate > line[3]) {
                minCoordinate = line[3];
            }
        }
    }

    private boolean isEqualsWithThreshold(Double distinctPointX, double[] coordinates) {
        return (Math.abs(coordinates[X1])) + OFFSET / 2 > distinctPointX && (Math.abs(coordinates[X1])) - OFFSET / 2 < distinctPointX;
    }

    private List<HorizontalLineCoordinate> formHorizontalLinesCoordinates(List<Double> lines, HorizontalLineCoordinate horizontalLineCoordinate) {
        List<HorizontalLineCoordinate> result = new ArrayList<>();
        for (Double line : lines) {
            result.add(new HorizontalLineCoordinate(horizontalLineCoordinate.getLeftPoint(), horizontalLineCoordinate.getRightPoint(), line));
        }
        return result;
    }

    public void saveIMageWithVerticalLines(List<double[]> linesV, int pageNumber) {
        try {
            for (double[] point : linesV) {
                Imgproc.line(verticalLinesMat, new Point(point[X1], point[Y1]), new Point(point[X2], point[Y2]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
            }
            Imgcodecs.imwrite(pageNumber + "_#9_rectVertical1.png", verticalLinesMat);
        } catch (Exception e) {
            throw new ImageVerticalLinesException("Image vertical lines saving failed", e);
        }
    }

    public void saveIMageWithVerticalLines2(List<double[]> linesV, int pageNumber) {
        try {
            for (double[] point : linesV) {
                Imgproc.line(verticalLinesMat, new Point(point[X1], point[Y1]), new Point(point[X2], point[Y2]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
            }
            Imgcodecs.imwrite(pageNumber + "_#9_rectVertical2.png", verticalLinesMat);
        } catch (Exception e) {
            throw new ImageVerticalLinesException("Image vertical lines saving failed", e);
        }
    }

    private void saveAllLines(List<VerticalLineCoordinate> verticalLine, List<HorizontalLineCoordinate> horizontalLines) {
        verticalLine.forEach(
                x -> Imgproc.line(verticalLinesMat, new Point(x.getXCoordinate(), x.getTopPoint()), new Point(x.getXCoordinate(), x.getBottomPoint()),
                        new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0));
        horizontalLines.forEach(
                x -> Imgproc.line(verticalLinesMat, new Point(x.getLeftPoint(), x.getYCoordinate()), new Point(x.getRightPoint(), x.getYCoordinate()),
                        new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0));
        Imgcodecs.imwrite("All_Lines.png", verticalLinesMat);
    }

    private List<Double> mergeLines(List<Double> pointsY) {
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

    private List<Double> sortLinesByY(List<double[]> lines) {
        List<Double> pointsY = new ArrayList<>();
        for (double[] line : lines) {
            pointsY.add(line[1]);
        }
        return pointsY.stream().sorted().toList();
    }

    private double[] sortListArray(List<double[]> lines) {
        double minY = lines.get(0)[Y1];
        double maxY = 0;
        double averageX = 0;
        for (double[] doubles : lines) {
            if (minY > doubles[Y1]) {
                minY = doubles[Y1];
            } else if (maxY < doubles[Y1]) {
                maxY = doubles[Y1];
            }
            if (minY > doubles[Y2]) {
                minY = doubles[Y2];
            } else if (maxY < doubles[Y2]) {
                maxY = doubles[Y2];
            }
            averageX = (averageX + doubles[X1]) / 2;
        }
        return new double[]{averageX, maxY, averageX, minY};
    }

    private List<Double> sortLinesByX(List<double[]> lines) {
        List<Double> result = new ArrayList<>();
        for (double[] line : lines) {
            result.add(line[X1]);
        }
        return result.stream().sorted().toList();
    }

    private HorizontalLineCoordinate findExtremeHorizontalTablePoints(List<double[]> lines) {
        double x1 = lines.get(0)[X1];
        double x2 = 0;
        for (double[] array : lines) {
            if (array[X1] < x1) {
                x1 = array[X1];
            }
            if (array[X2] > x2) {
                x2 = array[X2];
            }
        }
        return new HorizontalLineCoordinate(x1, x2, 0);
    }

    public List<double[]> findVerticalLinesWithOpenCV(String fileSource, int pageNumber) {
        Mat dst = new Mat();
        Mat cdst = new Mat();
        try {
        OpenCV.loadLocally();
        Mat source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);
        Imgproc.Canny(source, dst, ALL_LINES_THRESHOLD1, ALL_LINES_THRESHOLD2, ALL_LINES_APERTURESIZE, false);
        Imgcodecs.imwrite(pageNumber + "_#8_afterCannyRect.png", dst);
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        verticalLinesMat = cdst.clone();
        Mat linesP = new Mat();
        Imgproc.HoughLinesP(dst, linesP, ALL_LINES_RHO, Math.PI, (int) ALL_LINES_THRESHOLD2,
                RectangleDetector.VERTICAL_LINE_LENGTH, ALL_LINES_MAXLINEGAP);
        List<double[]> lines = new ArrayList<>();
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            lines.add(l);
        }
        return lines;
        } catch (Exception e) {
            throw new VerticalLinesOpenCVException("Vertical lines with openCV not find", e);
        }
    }

    private List<double[]> findHorizontalLinesWithOpenCV(String fileSource) {

        Mat dst = new Mat();
        Mat cdst = new Mat();
        Mat source;
        OpenCV.loadLocally();
        if (fileSource != null) {
            source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);
            int horizontalLineLength = (int) (source.width() * PERCENT_HORIZONTAL_LINE_LENGTH);
            Imgproc.Canny(source, dst, ALL_LINES_THRESHOLD1, ALL_LINES_THRESHOLD2, ALL_LINES_APERTURESIZE, false);
            if (!dst.empty()) {
                return new ArrayList<>();
            } else {
                Imgcodecs.imwrite("findhorizontalines_afterCannyRect.png", dst);
                Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
                Mat linesP = new Mat();
                Imgproc.HoughLinesP(dst, linesP, ALL_LINES_RHO, Math.PI / 2, HORIZONTAL_THRESHOLD,
                        horizontalLineLength, ALL_LINES_MAXLINEGAP);
                List<double[]> lines = new ArrayList<>();
                for (int x = 0; x < linesP.rows(); x++) {
                    double[] l = linesP.get(x, 0);
                    lines.add(l);
                }
                return lines;
            }
        } else {
            log.debug("{} is null.", fileSource);
            return new ArrayList<>();
        }
    }
}
