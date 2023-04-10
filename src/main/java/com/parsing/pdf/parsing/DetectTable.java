package com.parsing.pdf.parsing;

import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

@Slf4j
public class DetectTable {

    public static String detectTable(String fileSource) {
        OpenCV.loadLocally();
        Mat dst = new Mat(), cdst = new Mat(), cdstP;

        // Load an image
        Mat src = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);

        // Check if image is loaded fine
        if (src.empty()) {
            System.out.println("Error opening image!");
            System.out.println("Program Arguments: [image_name -- default "
                    + fileSource + "] \n");
            System.exit(-1);
        }

        // Edge detection
        Imgproc.Canny(src, dst, 50, 200, 3, false);
        Imgcodecs.imwrite("afterCanny.png", dst);

        // Copy edges to the images that will display the results in BGR
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        cdstP = cdst.clone();


        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI / 180, 25, 700, 10); // runs the actual detection

        // Draw the lines and search Points
        double xTopLeft = 500;
        double yTopLeft = 0;
        double xTopRight = 0;
        double yTopRight = 0;
        double xBottom = 0;
        double yBottom = 1500;
        System.out.println("lines rows = " + linesP.rows());
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            log.info(l[0] + " " + l[1] + " " + l[2] + " " + l[3]);
            Imgproc.line(cdstP, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
            if(l[0]<xTopLeft){
                xTopLeft= l[0];
            }

            if (l[1]>yTopLeft) {
                yTopLeft = l[1];
                xTopRight = l[2];
                yTopRight = l[3];
            }
            if (l[1]<yBottom) {
                yBottom = l[1];
            }
        }
        xBottom=xTopLeft;
        xTopLeft = 0;

        //Look for height and width rectangle
        double width = Math.sqrt((xTopRight - xTopLeft) * (xTopRight - xTopLeft) + (yTopRight - yTopLeft) * (yTopRight - yTopLeft));
        double heigth = Math.sqrt((xTopLeft - xTopLeft) * (xTopLeft - xTopLeft) + (yBottom - yTopLeft) * (yBottom - yTopLeft));
        //create Rectangle
        final Rect rectangle = new Rect((int) xBottom, (int) yBottom, (int) width, (int) heigth);
        //crop image
        Mat sub = new Mat();
        try {
            sub = src.submat(rectangle);
        } catch (Exception ex) {
            log.warn("can't crop image");
        }

        String fileResult = "destination.png";
        Imgcodecs.imwrite(fileResult, sub);

        return fileResult;
    }
}
