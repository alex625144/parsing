package com.parsing.parsers.pdf.parsing;

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
public class TableRecognizer {

    private static final int MINIMAL_QUANTITY_LINES_FOR_TABLE = 3;
    private static double x1 = 500;
    private static double y1 = 0;
    private static double x2 = 0;
    private static double y2 = 0;
    private static double y3 = 1500;
    private static int POWER_TWO = 2;

    private static Rect createRect(double x1, double y1, double x2, double y2, double y3) {
        double width = Math.sqrt(Math.pow((x2 - x1), POWER_TWO) + Math.pow((y2 - y1), POWER_TWO));
        double height = Math.sqrt(Math.pow((y3 - y2), POWER_TWO));
        return new Rect((int) x1, (int) y3, (int) width, (int) height);
    }

    public boolean isTableExistOnPage(String fileSource) {
        log.debug("Is table exist on page started.");
        OpenCV.loadLocally();
        Mat dst = new Mat(), cdst = new Mat(), cdstP, cropTable = new Mat();
        Mat source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);
        Imgproc.Canny(source, dst, 50, 200, 3, false);
        Imgcodecs.imwrite("afterCanny.png", dst);
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        cdstP = cdst.clone();
        Mat linesP = new Mat();
        Imgproc.HoughLinesP(dst, linesP, 3, Math.PI, 200, RectangleDetector.VERTICAL_LINE_LENGTH, 1);
        log.debug("Quantity lines " + linesP.size().toString());
        List<double[]> lines = new ArrayList<>();
        for (int k = 0; k < linesP.rows(); k++) {
            lines.add(linesP.get(k, 0));
        }
        if (lines.size() > MINIMAL_QUANTITY_LINES_FOR_TABLE) {
            log.debug("Result: table found!");
            log.debug("Is table exist on page finished.");
            return true;
        } else {
            log.debug("Result: table not found");
            log.debug("Is table exist on page finished.");
            return false;
        }

    }

    public String detectTable(String fileSource) {
        log.debug("Class TableRecognizer.detectTable started.");
        OpenCV.loadLocally();
        Mat dst = new Mat(), cdst = new Mat(), cdstP, cropTable = new Mat();
        Mat source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);
        Imgproc.Canny(source, dst, 50, 200, 3, false);
        Imgcodecs.imwrite("afterCanny.png", dst);
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        cdstP = cdst.clone();
        Mat linesP = new Mat();
        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI / 180, 25, 700, 10);
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            Imgproc.line(cdstP, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
            if (l[1] > y1) {
                y1 = l[1];
                x2 = l[2];
                y2 = l[3];
            }
            if (l[1] < y3) {
                y3 = l[1];
            }
            if (l[2] > x2) {
                x2 = l[2];
            }
            if (l[0] < x1) {
                x1 = l[0];
            }
        }
        Imgcodecs.imwrite("afterHough.png", cdstP);
        Rect rectangle = createRect(x1, y1, x2, y2, y3);
        try {
            cropTable = source.submat(rectangle);
            String fileResult = "destination.png";
            Imgcodecs.imwrite(fileResult, cropTable);
            log.debug("Class TableRecognizer.detectTable finished.");
            return fileResult;
        } catch (Exception ex) {
            log.warn("Can't crop image. Rectangle x = " + rectangle.x + " y = " + rectangle.y + " height = " + rectangle.height
                    + " width = " + rectangle.width + " source size = " + source.size());
        }
        return null;
    }
}