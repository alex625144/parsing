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
            apiParserService.parse();
        } catch (ProzorroParsingException ex) {
            throw new ProzorroParsingException(ex.getMessage());
        }
    }

    @GetMapping("/extract/{lotId}")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsingLot(@PathVariable String lotId) {
        try {
            apiParserService.parseInfo(lotId);
        } catch (ProzorroParsingException ex) {
            throw new ProzorroParsingException(ex.getMessage());
        }
    }

    @GetMapping("/extract/lotInformation")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsingLotInformation() {
        try {
            apiParserService.parseLotInformation();
        } catch (ProzorroParsingException ex) {
            throw new ProzorroParsingException(ex.getMessage());
        }
    }
}
