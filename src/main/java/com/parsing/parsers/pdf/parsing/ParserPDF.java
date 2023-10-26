package com.parsing.parsers.pdf.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.client.ChatGPTClient;
import com.parsing.client.ChatResponse;
import com.parsing.parsers.pdf.parsing.model.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import nu.pattern.OpenCV;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserPDF {

    private static final double OFFSET = 5;
    private static final double[] RGB_WHITE_COLOUR = {255, 255, 255};
    private static final String DIR_TO_READ_TESSDATA = "/tessdata/";
    private static final int PAGES_FOR_PARSE = 2;

    private final ObjectMapper objectMapper;
    private final RectangleDetector rectangleDetector;
    private final DataRecognizer dataRecognizer;
    private final TableRecognizer tableRecognizer;
    private final TableDetector tableDetector;
    private final PageOCRPreparator pageOCRPreparator;
    private final ChatGPTClient chatGPTClient;

    private List<Row> table = new ArrayList<>();

//    public String parseProzorroFile(MultipartFile file) throws IOException {
//        log.debug("Class ParserPDF.parseProzorroFile started");
//        OpenCV.loadLocally();
//        JSONObject obj = new JSONObject();
//        PDDocument document = PDDocument.load(file.getBytes());
//
//        for (int page = document.getNumberOfPages() - PAGES_FOR_PARSE; page < document.getNumberOfPages(); page++) {
//            final String prePage = pageOCRPreparator.preparePage(document, page);
//            if (tableRecognizer.isTableExistOnPage(prePage)) {
//                log.debug("Found table on page " + page);
//                List<double[]> lines = rectangleDetector.findVerticalLinesWithOpenCV(prePage);
//                log.debug("Quantity of lines = " + lines.size());
//                List<double[]> tablesLines = tableDetector.detectQuantityOfTables(lines);
//                rectangleDetector.saveIMageWithVerticalLines2(tablesLines);
//                String fileTableName = tableRecognizer.detectTable(prePage);
//                table = rectangleDetector.detectRectangles(fileTableName);
//                table = extractTextFromScannedDocument(fileTableName);
//                dataRecognizer.recognizeLotPDFResult(table);
//                obj.put("fileName", file.getOriginalFilename());
//                StringBuilder builder = new StringBuilder();
//                for (Row row : table) {
//                    for (Column column : row.getColumns()) {
//                        builder.append(column.getParsingResult());
//                    }
//                }
//                obj.put("text", builder);
//            } else {
//                log.debug("Table did not found on page " + page);
//            }
//        }
//        log.debug("Class ParserPDF.parseProzorroFile started");
//        return obj.toString();
//    }

    public boolean parseProzorroFileForScheduler(File file) throws IOException {
        log.debug("Class ParserPDF.parseProzorroFileForScheduler started");
        OpenCV.loadLocally();
        try (PDDocument document = PDDocument.load(file)) {
            for (int page = document.getNumberOfPages() - PAGES_FOR_PARSE; page < document.getNumberOfPages(); page++) {
                final String prePage = pageOCRPreparator.preparePage(document, page);
                if (tableRecognizer.isTableExistOnPage(prePage)) {
                    log.debug("Found table on page " + page);
                    List<double[]> lines = rectangleDetector.findVerticalLinesWithOpenCV(prePage);
                    log.debug("Quantity of lines = " + lines.size());
                    List<double[]> tablesLines = tableDetector.detectQuantityOfTables(lines);
                    rectangleDetector.saveIMageWithVerticalLines2(tablesLines);
                    String fileTableName = tableRecognizer.detectTable(prePage);
                    table = rectangleDetector.detectRectangles(fileTableName);
                    String hocr = extractTextFromScannedDocument(fileTableName);

                    // 2 tiktokenizer < 8192
                    ChatResponse response = chatGPTClient.execute(hocr);
                    //1 JsonNode
                    JsonNode jsonNode;

                    jsonNode = objectMapper.readTree(response.getChoices().get(0).getMessage().getContent());
                    //2 save to LaptomItem;


//                    boolean isRecognized = dataRecognizer.recognizeLotPDFResult(table);
//                    if (isRecognized) {
//                        log.debug("Class ParserPDF.parseProzorroFileForScheduler finished");
//                        return true;
//                    }
//                } else {
//                    log.debug("Table did not found on page " + page);
//                }
                }
            }
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
        log.debug("Class ParserPDF.parseProzorroFileForScheduler finished");
        return false;
    }

    private String getTessDataPath() {
        Path currentPathPosition = Paths.get("").toAbsolutePath();
        File pdfDir = new File(currentPathPosition + DIR_TO_READ_TESSDATA);
        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }
        return currentPathPosition.toAbsolutePath() + DIR_TO_READ_TESSDATA;
    }

    private String extractTextFromScannedDocument(String fileTableName) throws IOException, TesseractException {
        ITesseract itesseract = new Tesseract();
        itesseract.setDatapath(getTessDataPath());
        itesseract.setLanguage("ukr+eng");
        itesseract.setVariable("preserve_interword_spaces", "1");
        itesseract.setVariable("tessedit_create_hocr", "1");

        File temp = File.createTempFile(fileTableName, ".png");
        return itesseract.doOCR(temp);
        ;
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

    private boolean isTargetRectangle(Rectangle rectangle, int row, int column) {
        double yLeftUp = rectangle.getY() + OFFSET;
        double xLeftUp = rectangle.getX() + OFFSET;
        double xRightDown = xLeftUp + rectangle.getWidth() - OFFSET;
        double yRightDown = yLeftUp + rectangle.getHeight() - OFFSET;
        return xLeftUp < column && column < xRightDown && yLeftUp < row && row < yRightDown;
    }

    private String getLastPagePDF(PDDocument document) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        String lastPagePDF = "lastPagePDF.png";
        for (int page = document.getNumberOfPages() - 1; page < document.getNumberOfPages(); page++) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.GRAY);
            File temp = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(bim, "png", temp);
            File png = new File(lastPagePDF);
            ImageIO.write(bim, "png", png);
        }
        return lastPagePDF;
    }
}