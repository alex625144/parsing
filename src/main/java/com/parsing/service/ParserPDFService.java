package com.parsing.service;

import com.parsing.exception.PDFParsingException;
import com.parsing.parsers.pdf.parsing.ParserPDF;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class ParserPDFService {

    private final ParserPDF pdfParser;

    public boolean parsePDF(File file) {
        try {
            return pdfParser.parseProzorroFileForScheduler(file);
        } catch (Exception e) {
            throw new PDFParsingException("Parsing PDF file failed.", e);
        }
    }
}
