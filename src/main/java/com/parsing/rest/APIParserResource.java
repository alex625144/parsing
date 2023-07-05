package com.parsing.rest;

import com.parsing.exception.ProzorroSiteParseException;
import com.parsing.service.APIParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class APIParserResource {

    private final APIParserService apiParserService;

    @GetMapping("/api_lot_extract_all")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsing() {
        try {
            apiParserService.parse();
        } catch (ProzorroSiteParseException ex) {
            throw new ProzorroSiteParseException(ex.getMessage());
        }
    }
}
