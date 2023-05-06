package com.parsing.pdf.parsing;

import com.parsing.exception.PDFParsingException;
import com.parsing.pdf.parsing.modelParsing.Column;
import com.parsing.pdf.parsing.modelParsing.Row;
import com.parsing.repository.LotPDFResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParsingPDFService {

    private final LotPDFResultRepository lotPDFResultRepository;
    private final RectangleDetector rectangleDetector;
    private final Recognizer recognizer;

    public String parseProzorroFile(MultipartFile file) {

        String strippedTextFromFile = findStrippedTextFromFile(file);

        JSONObject obj = new JSONObject();
        obj.put("fileName", file.getOriginalFilename());
        obj.put("text", strippedTextFromFile);

        findData(obj.toString());

//        lotPDFResultRepository.save();

        return obj.toString();
    }

    private List<BigDecimal> findData(String input) {
        String model;
        int amount;
        BigDecimal price;
        BigDecimal totalPrice;

        String parsedModel = findModel(input);
        List<String> prices = findPrices(input);
        List<String> amounts = findAmount(input);

//        String[] split = input.split("\\n");
//        List<String> prices = Arrays.stream(split).filter(i -> i.matches(pricesPattern)).toList();
//        List<String> amounts = Arrays.stream(split).filter(a -> a.matches(amountPattern)).toList();

        //parse model, remove redundant parts

        List<BigDecimal> pricesConverted = prices.stream().map(p -> BigDecimal.valueOf(Double.parseDouble(p))).toList();
        List<Integer> amountsConverted = amounts.stream().map(Integer::parseInt).toList();
        for (BigDecimal prc : pricesConverted) {
            for (BigDecimal secondPrice : pricesConverted) {
                for (Integer amnt : amountsConverted) {
                    if (prc.longValueExact() * amnt == secondPrice.longValueExact()) {
                        price = prc;
                        totalPrice = secondPrice;
                        amount = amnt;
                    }
                }
            }
        }


        return null;
    }

    private String findModel(String input) {
//        String modelPattern = "Lenovo[\\s\\S]*?[а-щА-ЩЬьЮюЯя][а-щА-ЩЬьЮюЯя]";
        String modelPattern = "Lenovo[\\s\\S]*";
        Pattern pattern = Pattern.compile(modelPattern);
        Matcher matcher = pattern.matcher(input);

        String matchedSubstring = null;
        if (matcher.find()) {
            matchedSubstring = matcher.group();
            log.info("Matched model found: " + matchedSubstring);
        } else {
            log.info("No match for a model found");
        }
        return matchedSubstring;
    }

    public List<String> findPrices(String input) {
        String pricesPattern = "\\d{1,4}(?:\\s\\d{3})*(?:,\\d{2})?\n";
        Pattern pattern = Pattern.compile(pricesPattern);
        Matcher matcher = pattern.matcher(input);

        List<String> matchedPrices = new ArrayList<>();
        if (matcher.find()) {
            String match = matcher.group();
            matchedPrices.add(match);
            log.info("Matched price found: " + match);
        } else {
            log.info("No match for prices found");
        }
        return matchedPrices;
    }

    public List<String> findAmount(String input) {
        String amountPattern = "\\d{1,2}";
        Pattern pattern = Pattern.compile(amountPattern);
        Matcher matcher = pattern.matcher(input);

        List<String> matchedAmounts = new ArrayList<>();
        if (matcher.find()) {
            String match = matcher.group();
            matchedAmounts.add(match);
            log.info("Matched amount found: " + match);
        } else {
            log.info("No match for amount found");
        }
        return matchedAmounts;
    }

    private String findStrippedTextFromFile(MultipartFile file) {
        try {
            PDDocument document = PDDocument.load(file.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            String strippedText = stripper.getText(document);

            if (strippedText.trim().isEmpty()) {
                return extractTextFromScannedDocument(document);
            }
        } catch (Exception e) {
            throw new PDFParsingException(String.format("Parsing for file: {} failed.", file.getOriginalFilename()));
        }
        return null;
    }

    public String extractTextFromScannedDocument(PDDocument document)
            throws IOException, TesseractException {

        // Extract images from file
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder out = new StringBuilder();

//        // Define the folder path
//        Path userFilesFolderPath = Paths.get("tessdata");
//
//        // Create the folder if it doesn't exist
//        if (!Files.exists(userFilesFolderPath)) {
//            Files.createDirectory(userFilesFolderPath);
//        }

        ITesseract _tesseract = new Tesseract();
        _tesseract.setPageSegMode(1);
//        _tesseract.setDatapath("C:/Users/Maksym.Fedosov/Documents/tessdata/");
        _tesseract.setDatapath("E:/programming/projects/parsing/tessdata/");
//        _tesseract.setDatapath("../..//teseract/tessdata/");
//        _tesseract.setDatapath(userFilesFolderPath.toUri().toString());
        _tesseract.setLanguage("eng+ukr");
        log.info(document.getNumberOfPages() + "  pages in document");

        for (int page = document.getNumberOfPages() - 1; page < document.getNumberOfPages(); page++) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.GRAY);

            // Create a temp image file
            File temp = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(bim, "png", temp);

            File png = new File("008.png");
            ImageIO.write(bim, "png", png);

//            String tableName = detectTable(png.getName());
            List<Row> rows = rectangleDetector.detectRectangles("destination.png");

            for (Row row : rows) {
                for (Column column : row.getColumns()) {
                    String result = _tesseract.doOCR(new File(rows.indexOf(row) + "_" + row.getColumns().indexOf(column) + ".png"));
                    column.setParsingResult(result);
                    log.info("result " + result);
                    out.append(result);
                }
            }

            recognizer.recognizeLotPDFResult(rows);


            // Delete temp file
            temp.delete();

        }
        return out.toString();
    }

//    public Path findFilePath(String fileName) {
//        try {
//            // Define the folder path
//            Path userFilesFolderPath = Paths.get("tessdata");
//
//            // Create the folder if it doesn't exist
//            if (!Files.exists(userFilesFolderPath)) {
//                Files.createDirectory(userFilesFolderPath);
//            }
//
//            // Define the file path
//            return userFilesFolderPath.resolve(fileName);
//        } catch (IOException e) {
//            throw new RuntimeException("Error saving file: " + fileName, e);
//        }
//    }


}
