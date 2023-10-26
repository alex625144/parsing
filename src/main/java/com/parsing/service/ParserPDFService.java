package com.parsing.service;

import com.parsing.parsers.pdf.parsing.ParserPDF;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class ParserPDFService {

    private final ParserPDF pdfParser;

    @SneakyThrows
    public boolean parsePDF(File file) {
        return pdfParser.parseProzorroFileForScheduler(file);
    }
}