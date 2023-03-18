package com.parsing.pdf.parsing;

import com.parsing.exception.PDFParsingException;
import com.parsing.repository.LotPDFResultRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ParsingPDFService {

    private final LotPDFResultRepository lotPDFResultRepository;

    public String parseProzorroFile(MultipartFile file) {

        String strippedTextFromFile = findStrippedTextFromFile(file);

        JSONObject obj = new JSONObject();
        obj.put("fileName", file.getOriginalFilename());
        obj.put("text", strippedTextFromFile);

//        lotPDFResultRepository.save();

        return obj.toString();
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

        ITesseract _tesseract = new Tesseract();
        _tesseract.setDatapath("C:/Users/Maksym.Fedosov/Documents/tessdata/");
        _tesseract.setLanguage("ukr"); // choose your language

        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

            // Create a temp image file
            File temp = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(bim, "png", temp);

            String result = _tesseract.doOCR(temp);
            out.append(result);

            // Delete temp file
            temp.delete();

        }

        return out.toString();

    }
}
