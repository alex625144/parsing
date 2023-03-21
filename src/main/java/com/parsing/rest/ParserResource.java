package com.parsing.rest;

import com.parsing.parsers.parserlot.ParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController("/api/v1")
@RequiredArgsConstructor
public class ParserResource {

    private final ParserService parserService;

    @GetMapping("/parser-prozorro")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsing() {
        try {
            parserService.parse();
        } catch (RuntimeException | IOException ex) {
           ex.printStackTrace();
        }
    }
}
