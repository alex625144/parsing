package com.parsing.parsers.pdf.parsing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.Utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.opencv.core.Mat;
import org.opencv.core.Point;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class PageOCRPreparator {

    private final RotationImage rotationImage;

    private static final String DIR_TO_READ_TESSDATA = "/tessdata/";
    private static final int MINIMAL_WIDTH_WORD_FOR_OCR = 3;
    private static final int THICKNESS_LINE = 5;
    private static final double OFFSET = 5;
    private static final String REGEX = ".*[А-ЩЬЮЯЄЇІа-щьюяєїі].*";
    private static final double[] RGB_WHITE_COLOUR = {255, 255, 255};

    public String preparePage(PDDocument document, int page) throws IOException {
        log.debug("Class PageOCRPreparator started.");
        String fileResult = "preparePage" + page + ".png";
        String pagePDF = getPagePDF(document, page);
        String rotatedPage = rotationImage.rotate(pagePDF);
        Rectangle pageRectangle = getPageMainRectangle(document, page);
        Mat tableMat = Imgcodecs.imread(rotatedPage);
        Mat matPage = cleanTableBorders(tableMat, pageRectangle);
        Imgcodecs.imwrite(page + ".png", matPage);
        java.util.List<Rectangle> pageRectanglesAllWords = getPageRectanglesAllWords(page + ".png");
        List<Rectangle> rectanglesWithSymbols = extractTextFromRectangle(page + ".png", pageRectanglesAllWords);
        saveRectanglesOnImage(rectanglesWithSymbols, page + ".png");
        final Rectangle mainPageRectangle = findMainPageRectangle(rectanglesWithSymbols);
        final Mat result = cleanTableBorders(matPage, mainPageRectangle);
        Imgcodecs.imwrite(fileResult, result);
        log.debug("Class PageOCRPreparator finished.");
        return fileResult;
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
                    resultTemp = itesseract.doOCR(new File(filename), rectangle).trim();

                } catch (TesseractException e) {
                    e.printStackTrace();
                }
                if (resultTemp.matches(REGEX)) {
                    log.debug("OK " + resultTemp);
                    result.add(rectangle);
                } else {
                    log.debug(resultTemp);
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
        String pagePDF = "getPagePDF.png";
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
                String filename = pageNumber + "_PageMainRectangle.png";
                Imgcodecs.imwrite(filename, matrix);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rectangle;
    }

    private Rectangle findMainPageRectangle(List<Rectangle> rectangles) {

        if (rectangles.size() > 0) {
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
            return createRect(xMin, yMin, xMax, yMin, yMax);
        }
        return null;
    }

    private static Rectangle createRect(double x1, double y1, double x2, double y2, double y3) {
        double width = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)) * (1 + (OFFSET * 2 / 100));
        double height = Math.sqrt(Math.pow((y3 - y2), 2) * (1 + (OFFSET * 2 / 100)));
        return new Rectangle((int) (x1 - OFFSET * 2), (int) (y1 - OFFSET * 2), (int) width, (int) height);
    }

    private List<Rectangle> getPageRectanglesAllWords(String filename) {
        List<Rectangle> result = new ArrayList<>();
        ITesseract itesseract = new Tesseract();
        itesseract.setDatapath(getTessDataPath());
        itesseract.setLanguage("ukr+eng");
        BufferedImage bim = null;
        try {
            log.debug(getProjectPath() + filename);
            BufferedImage buf = ImageIO.read(new File(getProjectPath() + "\\" + filename));

            int level = ITessAPI.TessPageIteratorLevel.RIL_WORD;

            log.debug("PageIteratorLevel: " + Utils.getConstantName(level, ITessAPI.TessPageIteratorLevel.class));
            result = itesseract.getSegmentedRegions(buf, level);
            for (int i = 0; i < result.size(); i++) {
                Rectangle rect = result.get(i);
                log.debug(String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d", i, rect.x, rect.y, rect.width, rect.height));
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

                String filename2 = filename + "_AllWords.png";
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

    private Mat cleanTableBorders(Mat image, Rectangle rectangle) {
        Mat result = image.clone();
        for (int row = 0; row < image.rows(); row++) {
            for (int column = 0; column < image.cols(); column++) {
                if (!isTargetRectangle(rectangle, row, column)) {
                    result.put(row, column, RGB_WHITE_COLOUR);
                }
            }
        }
        return result;
    }

    private String saveRectanglesOnImage(List<Rectangle> rectangles, String filenameImage) {
        String filenameResult = null;
        if (rectangles.size() > 0) {
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

            filenameResult = filenameImage + "saveRectanglesOnImage.png";
            Imgcodecs.imwrite(filenameResult, matrix);
        }
        return filenameResult;
    }

    private boolean isTargetRectangle(Rectangle rectangle, int row, int column) {
        double yLeftUp = rectangle.getY() + OFFSET;
        double xLeftUp = rectangle.getX() + OFFSET;
        double xRightDown = xLeftUp + rectangle.getWidth() - OFFSET;
        double yRightDown = yLeftUp + rectangle.getHeight() - OFFSET;
        return xLeftUp < column && column < xRightDown && yLeftUp < row && row < yRightDown;
    }
}

