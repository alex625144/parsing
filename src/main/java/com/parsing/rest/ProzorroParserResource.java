package com.parsing.rest;

import com.parsing.exception.PDFParsingException;
import com.parsing.exception.ProzorroSiteParseException;
import com.parsing.schedulers.Scheduler;
import com.parsing.service.ProzorroParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ProzorroParserResource {

    private final ProzorroParserService parserService;
    private final Scheduler schedulerParsingPDF;

    @GetMapping("/parser-prozorro")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsing() {
        try {
            parserService.parse();
        } catch (ProzorroSiteParseException ex) {
            throw new ProzorroSiteParseException(ex.getMessage());
        }
    }

    @GetMapping("/download-and-parse-prozorro")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsingPDF() {
        try {
            schedulerParsingPDF.parsLotResultAndLotPDFResult();
        } catch (PDFParsingException ex) {
            throw new PDFParsingException(ex.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
