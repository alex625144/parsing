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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TableRecognizer {

    private final PageOCRPreparator pageOCRPreparator;
    private static final int MINIMAL_QUANTITY_LINES_FOR_TABLE = 3;
    private static double x1, y1, x2, y2, y3;
    private static int POWER_TWO = 2;

    private static Rect createRect(double x1, double y1, double x2, double y2, double y3) {
        double width = Math.sqrt(Math.pow((x2 - x1), POWER_TWO));
        double height = Math.sqrt(Math.pow((y3 - y2), POWER_TWO));
        return new Rect((int) x1, (int) y1, (int) width, (int) height);
    }

    public Rectangle createRectangle(double xLeftBottom, double yLeftBottom, double xRightBottom, double yRightBottom, double yRightTop) {
        log.debug("Method createRectangle started.");
        double width = Math.sqrt(Math.pow((xRightBottom - xLeftBottom), 2) + Math.pow((yRightBottom - yLeftBottom), 2));
        double height = Math.sqrt(Math.pow((yRightTop - yRightBottom), 2));
        log.debug("Method createRectangle finished.");
        return new Rectangle((int) xLeftBottom, (int) yLeftBottom, (int) width, (int) height);
    }

    public boolean isTableExistOnPage(String fileSource) {
        log.debug("Method IsTableExistOnPage started.");
        OpenCV.loadLocally();
        Mat dst = new Mat(), cdst = new Mat(), cdstP, cropTable = new Mat();
        Mat source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);
        Imgproc.Canny(source, dst, 50, 200, 3, false);
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        cdstP = cdst.clone();
        Mat linesP = new Mat();
        Imgproc.HoughLinesP(dst, linesP, 3, Math.PI, 200, RectangleDetector.VERTICAL_LINE_LENGTH, 1);
        log.debug("Quantity of lines founded =  " + linesP.size().toString());
        List<double[]> lines = new ArrayList<>();
        for (int k = 0; k < linesP.rows(); k++) {
            lines.add(linesP.get(k, 0));
        }
        if (lines.size() > MINIMAL_QUANTITY_LINES_FOR_TABLE) {
            log.debug("Result: table found!");
            log.debug("Method IsTableExistOnPage finished.");
            return true;
        } else {
            log.debug("Result: table not found");
            log.debug("Method IsTableExistOnPage finished.");
            return false;
        }
    }

    public String detectTable(String fileSource, int page) {
        log.debug("Method detectTable started.");
        OpenCV.loadLocally();
        Mat dst = new Mat(), cdst = new Mat(), cdstP, cropTable = new Mat();
        Mat source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);
        Imgproc.Canny(source, dst, 50, 200, 3, false);
        Imgcodecs.imwrite(page + "_#7_afterCanny.png", dst);
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        cdstP = cdst.clone();
        Mat linesP = new Mat();
        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI / 180, 25, 700, 10);
        ///initialize
        double[] array = linesP.get(0, 0);
        x1 = array[0];
        y1 = array[1];
        x2 = array[2];
        y2 = array[3];
        y3 = array[3];

        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            Imgproc.line(cdstP, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
            if (l[0] < x1) {
                x1 = l[0];
            }
            if (l[1] < y1) {
                y1 = l[1];
            }
            if (l[2] > x2) {
                x2 = l[2];
            }
            if (l[3] > y3) {
                y3 = l[3];
            }

        }
        Imgcodecs.imwrite(page + "_#8_afterHough.png", cdstP);
        log.debug("x1 = {}; y1 = {}; x2 = {}; y2={}, y3={}", x1, y1, x2, y2, y3);
        Rect rect = createRect(x1, y1, x2, y2, y3);
        try {
                Rect rectNew = isImageConsistRectangle(rect, source);
                cropTable = new Mat(source, rectNew);
                String fileResult = page + "_#10_destination.png";
                Imgcodecs.imwrite(fileResult, cropTable);
                log.debug("Method detectTable finished.");
                return fileResult;
        } catch (Exception ex) {
            log.warn("Can't crop image. Rectangle x = " + rect.x + " y = " + rect.y + " height = " + rect.height
                    + " width = " + rect.width + " source size = " + source.size(), ex.getCause());
        }
        return null;
    }

    private Rect isImageConsistRectangle(Rect rectangle, Mat image){
        if ((rectangle.x + rectangle.width >= (double) image.width())
                && (rectangle.y + rectangle.height >= (double) image.height())) {
            return rectangle;
        } else{
            x2 = image.width();
            y3 = image.height() - rectangle.y;
            return createRect(rectangle.x, rectangle.y, x2, rectangle.y, y3);
        }
    }
}