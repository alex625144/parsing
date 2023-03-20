package com.parsing.rest;

import com.parsing.parsers.parserlot.ParserLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class ParserResource {

    private final ParserLotService parserLotService;

    @GetMapping("/parser_prozorro")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsing () throws IOException {
        parserLotService.parse();
    }
}
