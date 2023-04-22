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

    static final Double OFFSET = 5.0;
    Mat cdstP = null;
    private final TableProcessor tableProcessor;



    public String detectRectangles(String fileSource) {

        List<double[]> lines = findHorizontalLinesWithOpenCV(fileSource);
        List<Double> sortedPointsY = sortList(lines);
        List<Double> distinctPointsY = mergeGorizontalLines(sortedPointsY);
        TableWidth pointsWidthTable = findPointsWidthTable(lines);
        for (Double point : distinctPointsY) {
            Imgproc.line(cdstP, new Point(pointsWidthTable.getLeftPoint(), point), new Point(pointsWidthTable.getRightPoint(), point), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
        }
        Imgcodecs.imwrite("afterDistinct.png", cdstP);

        List<Rect> rects = tableProcessor.cropRowsRectangles(distinctPointsY, pointsWidthTable);

        Mat source = Imgcodecs.imread("destination.png", Imgcodecs.IMREAD_GRAYSCALE);
        Mat cropRect = new Mat();
        for (int i = 0; i<rects.size(); i++) {
            try {
                cropRect = source.submat(rects.get(i));
            } catch (Exception ex) {
                log.warn("can't crop image");
            }
            Imgcodecs.imwrite("rect"+i+".png",cropRect);
        }

        String fileResult = "destinationRect.png";

        return fileResult;
    }

    private List<Double> mergeGorizontalLines(List<Double> pointsY) {
        System.out.println("Lines has rows at start= " + pointsY.size());
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
        System.out.println("Lines has rows at finished= " + result.size());
        return result;
    }

    private List<Double> sortList(List<double[]> lines) {
        List<Double> pointsY = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            pointsY.add(lines.get(i)[1]);
        }
        return pointsY.stream().sorted().toList();
    }

    private TableWidth findPointsWidthTable(List<double[]> lines) {
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
        return new TableWidth(x1, x2);
    }

    private List<double[]> findHorizontalLinesWithOpenCV(String fileSource) {
        Mat dst = new Mat(), cdst = new Mat(), cropTable = new Mat();
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
        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI / 180, 25, 700, 10);

        List<double[]> lines = new ArrayList<>();

        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            lines.add(l);
        }
        return lines;
    }
}
