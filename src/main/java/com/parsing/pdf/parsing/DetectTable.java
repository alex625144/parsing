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

@Slf4j
@Component
@RequiredArgsConstructor
public class DetectTable {

    private static double x1 = 500;
    private static double y1 = 0;
    private static double x2 = 0;
    private static double y2 = 0;
    private static double y3 = 1500;
    private final TableProcessor tableProcessor;

    public static String detectTable(String fileSource) {
        OpenCV.loadLocally();
        Mat dst = new Mat(), cdst = new Mat(), cdstP, cropTable = new Mat();
        Mat source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);

        if (source.empty()) {
            log.warn("Error opening image!");
            log.warn("Program Arguments: [image_name -- default "
                    + fileSource + "] \n");
            System.exit(-1);
        }

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
        } catch (Exception ex) {
            log.warn("can't crop image");
        }
        String fileResult = "destination.png";
        Imgcodecs.imwrite(fileResult, cropTable);

        return fileResult;
    }

    private static Rect createRect(double x1, double y1, double x2, double y2, double y3) {
        double width = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        double height = Math.sqrt(Math.pow((y3 - y2), 2));
        return new Rect((int) x1, (int) y3, (int) width, (int) height);
    }
}
