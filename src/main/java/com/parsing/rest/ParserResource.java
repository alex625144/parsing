package com.parsing.rest;

import com.parsing.parsers.parserLot.ParserLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ParserResource {

    private final ParserLotService parserLotService;

    @GetMapping("/parserProzorro")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsing () throws IOException {
        List<String> urls = parserLotService.prepareURI();
        parserLotService.parser(urls);
    }
}
