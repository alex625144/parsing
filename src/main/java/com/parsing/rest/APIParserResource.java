package com.parsing.rest;

import com.parsing.exception.ProzorroSiteParseException;
import com.parsing.service.APIParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;

@RestController
@RequiredArgsConstructor
public class APIParserResource {

    private final APIParserService apiParserService;

    @GetMapping("/api_lot_extract_all")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsingId() {
        try {
            apiParserService.parse();
        } catch (ProzorroSiteParseException ex) {
            throw new ProzorroSiteParseException(ex.getMessage());
        }
    }

    @GetMapping("/{lotId}")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsingLot(@PathVariable String lotId) {
        try {
            apiParserService.parseInfo(lotId);
        } catch (ProzorroSiteParseException ex) {
            throw new ProzorroSiteParseException(ex.getMessage());
        }
    }
}
