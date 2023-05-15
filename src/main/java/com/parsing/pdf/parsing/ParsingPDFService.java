package com.parsing.pdf.parsing;

import com.parsing.exception.PDFParsingException;
import com.parsing.pdf.parsing.model.Column;
import com.parsing.pdf.parsing.model.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import nu.pattern.OpenCV;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParsingPDFService {

    private static final double OFFSET = 5;
    private static final int WHITE_PIXEL_CODE = 255;
    private final RectangleDetector rectangleDetector;
    private final DataRecognizer dataRecognizer;

    public String parseProzorroFile(MultipartFile file) {
        OpenCV.loadLocally();
        List<Row> table1 = findStrippedTextFromFile(file);
        dataRecognizer.recognizeLotPDFResult(table1);

        JSONObject obj = new JSONObject();
        obj.put("fileName", file.getOriginalFilename());
        StringBuilder builder = new StringBuilder();
        for (Row row : table1) {
            for (Column column : row.getColumns()) {
                builder.append(column.getParsingResult());
            }
        }

        obj.put("text", builder);
        return obj.toString();
    }

    private List<Row> findStrippedTextFromFile(MultipartFile file) {
        try {
            PDDocument document = PDDocument.load(file.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            String strippedText = stripper.getText(document);
            if (strippedText.trim().isEmpty()) {
                return extractTextFromScannedDocument(document);
            }
        } catch (Exception e) {
            throw new PDFParsingException(String.format("Parsing for file: {0} failed.", file.getOriginalFilename()));
        }
        return null;
    }

    private List<Row> extractTextFromScannedDocument(PDDocument document) throws IOException, TesseractException {
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        List<Row> table = new ArrayList<>();

        ITesseract _tesseract = new Tesseract();
//        _tesseract.setDatapath("C:/Users/Maksym.Fedosov/Documents/tessdata/");
        _tesseract.setDatapath("E:/programming/projects/parsing/tessdata/");
        _tesseract.setLanguage("ukr+eng");
        log.info(document.getNumberOfPages() + "  pages in document");

        for (int page = document.getNumberOfPages() - 1; page < document.getNumberOfPages(); page++) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.GRAY);

            // Create a temp image file
            File temp = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(bim, "png", temp);

            File png = new File("008.png");
            ImageIO.write(bim, "png", png);

            String fileTableName = "destination.png";
            table = rectangleDetector.detectRectangles(fileTableName);

            final Mat tableMat = Imgcodecs.imread(fileTableName);
            for (Row row : table) {
                for(Column column: row.getColumns()) {
                    Mat mat = fillSquareWhitePixel(tableMat, column.getRectangle());
                    Imgcodecs.imwrite(table.indexOf(row) + " " + row.getColumns().indexOf(column) + ".png", mat);
                }
            }
            for (Row row : table) {
                for (Column column : row.getColumns()) {
                    String filename = table.indexOf(row) + " " +row.getColumns().indexOf(column) + ".png";
                    String result = _tesseract.doOCR(new File(filename));
                    column.setParsingResult(result);
                    log.info(filename + " = " + result);
                }
            }
        }
        return table;
    }

    private Mat fillSquareWhitePixel(Mat image, Rectangle rectangle) {
        Mat result = image.clone();
        double[] whitePixel = {WHITE_PIXEL_CODE, WHITE_PIXEL_CODE, WHITE_PIXEL_CODE};
        double xLeftUp = rectangle.getX() + OFFSET;
        double yLeftUp = rectangle.getY() + OFFSET;
        double xRightDown = xLeftUp + rectangle.getWidth() - OFFSET;
        double yRightDown = yLeftUp + rectangle.getHeight() - OFFSET;
        for (int row = 0; row < image.rows(); row++) {
            for (int column = 0; column < image.cols(); column++) {
                if (xLeftUp < column && column < xRightDown && yLeftUp < row && row < yRightDown) {
                } else {
                    result.put(row, column, whitePixel);
                }
            }
        }
        return result;
    }
}
