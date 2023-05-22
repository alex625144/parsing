package com.parsing.rest;

import com.parsing.schedulers.Scheduler;
import com.parsing.service.ProzorroParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController()
@RequiredArgsConstructor
public class ProzorroParserResource {

    private final ProzorroParserService parserService;
    private final Scheduler schedulerParsingPDF;

    @GetMapping("/parser-prozorro")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsing() {
        try {
            parserService.parse();
        } catch (RuntimeException | IOException ex) {
            ex.printStackTrace();
        }
    }

    @GetMapping("/download-and-parse-prozorro")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsingPDF() {
        try {
            schedulerParsingPDF.scheduled();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
