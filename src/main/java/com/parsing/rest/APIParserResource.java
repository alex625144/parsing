package com.parsing.rest;

import com.parsing.exception.ProzorroParsingException;
import com.parsing.service.APIParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController("/api/v1")
@RequiredArgsConstructor
public class APIParserResource {

    private final APIParserService apiParserService;

    @GetMapping("/extract/lotId")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsingId() {
        try {
            apiParserService.parseLotId();
        } catch (ProzorroParsingException e) {
            throw new ProzorroParsingException("Parsing by id failed.", e);
        }
    }

    @GetMapping("/extract/{lotId}")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsingLot(@PathVariable String lotId) {
        try {
            apiParserService.parseLotInformation(lotId);
        } catch (ProzorroParsingException e) {
            throw new ProzorroParsingException("Parsing lot by id failed.", e);
        }
    }

    @GetMapping("/extract/lotInformation")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsingLotInformation() {
        try {
            apiParserService.parseLotInformation();
        } catch (ProzorroParsingException e) {
            throw new ProzorroParsingException("Parsing lot information failed.", e);
        }
    }
}
