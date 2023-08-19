package com.parsing.service;

import com.parsing.parsers.pdf.parsing.ParserPDF;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ParserPDFService {

    private final ParserPDF pdfParser;

    public boolean parsePDF(File file) {
        try {
            return pdfParser.parseProzorroFileForScheduler(file);
        } catch (IOException e) {
            throw new RuntimeException("File was not parsed.",e);
        }
    }
}
