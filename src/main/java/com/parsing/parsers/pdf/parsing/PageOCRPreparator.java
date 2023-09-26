package com.parsing.parsers.pdf.parsing;

import com.parsing.Constants;
import com.parsing.exception.PreparePageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.LoadLibs;
import net.sourceforge.tess4j.util.Utils;
import nu.pattern.OpenCV;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static com.parsing.Constants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class PageOCRPreparator {

    private final RotationImage rotationImage;
    private static final String DIR_TO_READ_TESSDATA = File.separator + "tessdata" + File.separator;
    private static final int MINIMAL_WIDTH_WORD_FOR_OCR = 3;
    private static final int THICKNESS_LINE = 5;
    private static final String REGEX = ".*[А-ЩЬЮЯЄЇІа-щьюяєїі].*";
    private static final double[] RGB_WHITE_COLOUR = {255, 255, 255};
    static final double ALL_LINES_THRESHOLD1 = 50;
    static final double ALL_LINES_THRESHOLD2 = 200;
    static final int ALL_LINES_APERTURESIZE = 3;
    static final int ALL_LINES_RHO = 3;
    static final int ALL_LINES_MAXLINEGAP = 5;

    public String preparePage(PDDocument document, int pageNumber) throws IOException {

        try {
            log.info("Method pageOCRPreparator started.");
            String fileResult = pageNumber + "_#6_prepared_page.png";
            String pagePDF = getPagePDF(document, pageNumber);
            String rotatedPage = rotationImage.rotateImage(pagePDF, pageNumber);
            List<Rect> rects = extractListRect(rotatedPage);
            Mat image = Imgcodecs.imread(rotatedPage);
            final Mat result2 = cleanAllBesidesRects(image, rects, pageNumber);
            Imgcodecs.imwrite(pageNumber + "_#3_cleanAllBesidesRects.png", result2);
            java.util.List<Rectangle> pageRectanglesAllWords = getPageRectanglesAllWords(pageNumber + "_#3_cleanAllBesidesRects.png", pageNumber);
            List<Rectangle> rectanglesWithSymbols = extractTextFromRectangle(pageNumber + "_#2_rotatedImage.png", pageRectanglesAllWords);
            saveRectanglesOnImage(rectanglesWithSymbols, pageNumber + "_#2_rotatedImage.png", pageNumber);
            Rectangle mainPageRectangle = findMainPageRectangle(rectanglesWithSymbols);
            List<double[]> verticalLinesWithOpenCV = findVerticalLinesWithOpenCV(pageNumber + "_#2_rotatedImage.png");

            double offset = getOffset(mainPageRectangle, verticalLinesWithOpenCV);
            mainPageRectangle = offset > 0 ? createMainRect(mainPageRectangle, offset) : mainPageRectangle;

            Mat tableMat = Imgcodecs.imread(rotatedPage);
            final Mat result = cleanTableBorders(tableMat, mainPageRectangle, pageNumber);
            Imgcodecs.imwrite(fileResult, result);
            log.info("Method PageOCRPreparator finished.");
            return fileResult;
        } catch (RuntimeException ex) {
            throw new PreparePageException(ex.getMessage());
        } finally {
            return "";
        }
    }

    private List<Rectangle> extractTextFromRectangle(String filename, List<Rectangle> rectangles) {
        log.debug("start rectangles = " + rectangles.size());
        ITesseract itesseract = new Tesseract();
        itesseract.setDatapath(getTessDataPath());
        itesseract.setLanguage("ukr+eng");
        List<Rectangle> result = new ArrayList<>();
        final Mat tableMat = Imgcodecs.imread(filename);
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getWidth() >= MINIMAL_WIDTH_WORD_FOR_OCR) {
                String resultTemp = null;
                try {
                    resultTemp = itesseract.doOCR(new File(getProjectPath() + File.separator + filename), rectangle).trim();
                } catch (TesseractException e) {
                    e.printStackTrace();
                }
                if (resultTemp != null && resultTemp.matches(REGEX)) {
                    result.add(rectangle);
                }
            }
        }
        log.debug("end rectangles = " + result.size());
        return result;
    }

    private String getTessDataPath() {
        Path currentPathPosition = Paths.get("").toAbsolutePath();
        File pdfDir = new File(currentPathPosition + DIR_TO_READ_TESSDATA);
        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }
        return currentPathPosition.toAbsolutePath() + DIR_TO_READ_TESSDATA;
    }

    private String getPagePDF(PDDocument document, int pageNumber) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        String pagePDF = pageNumber + "_#1_sourcePage.png";
        for (int page = 0; page < document.getNumberOfPages(); page++) {
            if (page == pageNumber) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.GRAY);
                File temp = File.createTempFile("tempfile_" + page, ".png");
                ImageIO.write(bim, "png", temp);
                File png = new File(pagePDF);
                ImageIO.write(bim, "png", png);
            }
        }
        return pagePDF;
    }

    private Rectangle getPageMainRectangle(PDDocument document, int pageNumber) {
        Rectangle rectangle = new Rectangle();
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        ITesseract itesseract = new Tesseract();
        itesseract.setDatapath(getTessDataPath());
        itesseract.setLanguage("ukr+eng");
        List<Word> result = new ArrayList<>();
        BufferedImage bim = null;
        try {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNumber, 300, ImageType.RGB);
            result = itesseract.getWords(bufferedImage, ITessAPI.TessPageIteratorLevel.RIL_BLOCK);
            if (result.size() > 0) {
                rectangle = result.get(0).getBoundingBox().getBounds();
                Mat matrix = Imgcodecs.imread(getPagePDF(document, pageNumber));
                Imgproc.rectangle(
                        matrix,
                        new org.opencv.core.Point(rectangle.getMinX(), rectangle.getMaxY()),
                        new Point(rectangle.getMaxX(), rectangle.getMinY()),
                        new Scalar(0, 0, 255),
                        THICKNESS_LINE);
                String filename = pageNumber + "_#4_PageMainRectangle.png";
                Imgcodecs.imwrite(filename, matrix);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rectangle;
    }

    private Rectangle findMainPageRectangle(List<Rectangle> rectangles) {
        if (!rectangles.isEmpty()) {
            double xMin = rectangles.get(0).getX();
            double yMin = rectangles.get(0).getY();
            double xMax = rectangles.get(0).getX();
            double yMax = rectangles.get(0).getY();

            for (Rectangle rectangle : rectangles) {
                double xCurrent = rectangle.getX();
                double yCurrent = rectangle.getY();
                if (xCurrent < xMin) {
                    xMin = xCurrent;
                }
                if (xCurrent > xMax) {
                    xMax = xCurrent;
                }
                if (yCurrent < yMin) {
                    yMin = yCurrent;
                }
                if (yCurrent > yMax) {
                    yMax = yCurrent;
                }
            }
            return createRect(xMin, yMin, xMax, yMin, yMax, Constants.OFFSET);
        }
        return null;
    }

    private static Rectangle createRect(double x1, double y1, double x2, double y2, double y3, int OFFSET) {
        double width = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)) * (1 + (OFFSET * 2 / 100));
        double height = Math.sqrt(Math.pow((y3 - y2), 2) * (1 + (OFFSET * 2 / 100)));
        return new Rectangle((int) (x1 - OFFSET * 2), (int) (y1 - OFFSET * 2), (int) width, (int) height);
    }

    private List<Rectangle> getPageRectanglesAllWords(String filename, int pageNumber) {

        List<Rectangle> result = new ArrayList<>();
        ITesseract itesseract = new Tesseract();
        itesseract.setDatapath(getTessDataPath());
        itesseract.setLanguage("ukr+eng");
        BufferedImage bim = null;
        try {
            log.debug(getProjectPath() + File.separator + filename);
            BufferedImage buf = ImageIO.read(new File(getProjectPath() + File.separator + filename));

            int level = ITessAPI.TessPageIteratorLevel.RIL_WORD;

            log.debug("PageIteratorLevel: " + Utils.getConstantName(level, ITessAPI.TessPageIteratorLevel.class));
            result = itesseract.getSegmentedRegions(buf, level);
            for (int i = 0; i < result.size(); i++) {
                Rectangle rect = result.get(i);
                //log.debug(String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d", i, rect.x, rect.y, rect.width, rect.height));
            }

            if (result.size() > 0) {
                Mat matrix = Imgcodecs.imread(filename);
                for (Rectangle rectangle : result) {
                    rectangle = rectangle.getBounds();
                    Imgproc.rectangle(
                            matrix,
                            new Point(rectangle.getMinX(), rectangle.getMaxY()),
                            new Point(rectangle.getMaxX(), rectangle.getMinY()),
                            new Scalar(0, 0, 255),
                            THICKNESS_LINE);
                }

                String filename2 = pageNumber + "_#3_allWordsOnPage.png";
                Imgcodecs.imwrite(filename2, matrix);
            } else {
                log.debug("Text not found on page.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getProjectPath() {
        Path currentPathPosition = Paths.get("").toAbsolutePath();
        return currentPathPosition.toAbsolutePath().toString();
    }

    private Mat cleanTableBorders(Mat image, Rectangle rectangle, int pageNumber) {
        Mat result = image.clone();
        for (int row = 0; row < image.rows(); row++) {
            for (int column = 0; column < image.cols(); column++) {
                if (!isTargetRectangle(rectangle, row, column)) {
                    result.put(row, column, RGB_WHITE_COLOUR);
                }
            }
        }
        Imgcodecs.imwrite(pageNumber + "_#5_cleanTableBorders.png", result);
        return result;
    }

    public static String saveRectanglesOnImage(List<Rectangle> rectangles, String filenameImage, int pageNumber) {
        String filenameResult = null;
        if (!rectangles.isEmpty()) {
            Mat matrix = Imgcodecs.imread(filenameImage);
            for (Rectangle rectangle : rectangles) {
                rectangle = rectangle.getBounds();
                Imgproc.rectangle(
                        matrix,
                        new Point(rectangle.getMinX(), rectangle.getMaxY()),
                        new Point(rectangle.getMaxX(), rectangle.getMinY()),
                        new Scalar(0, 0, 255),
                        THICKNESS_LINE);
            }

            filenameResult = pageNumber + "_#4_savedImage.png";
            Imgcodecs.imwrite(filenameResult, matrix);
        }
        return filenameResult;
    }

    private static Rectangle createMainRect(Rectangle rectangle, double offset) {
        log.debug("offset = " + offset);
        double width = rectangle.width + offset * 2;
        double height = rectangle.getHeight();
        return new Rectangle((int) (rectangle.getX() - offset), rectangle.y, (int) width, (int) height);
    }

    private boolean isTargetRectangle(Rectangle rectangle, int row, int column) {
        double yLeftUp = rectangle.getY();
        double xLeftUp = rectangle.getX();
        double xRightDown = xLeftUp + rectangle.getWidth();
        double yRightDown = yLeftUp + rectangle.getHeight();
        return xLeftUp < column && column < xRightDown && yLeftUp < row && row < yRightDown;
    }

    private boolean isTargetRect(Rect rectangle, int row, int column) {
        double yLeftUp = rectangle.y;
        double xLeftUp = rectangle.x;
        double xRightDown = xLeftUp + rectangle.width;
        double yRightDown = yLeftUp + rectangle.height;
        return xLeftUp <= column && column <= xRightDown && yLeftUp <= row && row <= yRightDown;
    }

    private double getOffset(Rectangle rectangle, List<double[]> list) {
        double rightOffset;
        double leftOffset;
        double leftRectangleX = rectangle.getX();
        double rightRectangeX = rectangle.getX() + rectangle.width;
        double rightListX = list.get(0)[0];
        double leftListX = list.get(0)[2];
        for (double[] doubles : list) {
            if (rightListX < doubles[0]) {
                rightListX = doubles[0];
            }
            if (leftListX > doubles[2]) {
                leftListX = doubles[2];
            }
        }
        leftOffset = leftRectangleX > leftListX ? leftRectangleX - leftListX : 0;
        rightOffset = rightListX > rightRectangeX ? rightListX - rightRectangeX : 0;
        return Math.max(rightOffset, leftOffset);
    }

    private List<double[]> findVerticalLinesWithOpenCV(String fileSource) {
        Mat dst = new Mat();
        Mat cdst = new Mat();
        OpenCV.loadLocally();
        Mat source = Imgcodecs.imread(fileSource, Imgcodecs.IMREAD_GRAYSCALE);
        Imgproc.Canny(source, dst, ALL_LINES_THRESHOLD1, ALL_LINES_THRESHOLD2, ALL_LINES_APERTURESIZE, false);
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        Mat linesP = new Mat();
        Imgproc.HoughLinesP(dst, linesP, ALL_LINES_RHO, Math.PI, (int) ALL_LINES_THRESHOLD2,
                RectangleDetector.VERTICAL_LINE_LENGTH, ALL_LINES_MAXLINEGAP);
        List<double[]> lines = new ArrayList<>();
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            lines.add(l);
        }
        return lines;
    }

    private List<Rect> extractListRect(String filesource) {
        Mat source = Imgcodecs.imread(filesource, Imgcodecs.IMREAD_GRAYSCALE);
        double width = source.width();
        double heigth = source.height();
        Rect LeftRect = new Rect(0, 0, (int) (width * PERCENT_PAGE_FOR_OCR), (int) heigth);
        Rect RigthRect = new Rect((int) (width - width * PERCENT_PAGE_FOR_OCR), 0,
                (int) (width * PERCENT_PAGE_FOR_OCR), (int) heigth);
        return List.of(LeftRect, RigthRect);
    }

    private Mat cleanAllBesidesRects(Mat image, List<Rect> rectangles, int pageNumber) {
        Mat result = image.clone();
        for (int row = 0; row < image.rows(); row++) {
            for (int column = 0; column < image.cols(); column++) {
                if (!isTargetRect(rectangles.get(0), row, column) &&
                        !isTargetRect(rectangles.get(1), row, column)) {
                    result.put(row, column, RGB_WHITE_COLOUR);
                }
            }
        }
        return result;
    }
}

