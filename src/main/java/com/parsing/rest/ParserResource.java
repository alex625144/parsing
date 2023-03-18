package com.parsing.rest;

import com.parsing.parsers.parserLot.ParserLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ParserResource {

    private final ParserLotService parserLotService;
    LocalDate START_DATE = LocalDate.of(2023, 02, 21);
    LocalDate END_DATE = LocalDate.of(2023, 02, 21);

    @GetMapping("/parserProzorro")
    @ResponseStatus(HttpStatus.FOUND)
    public void parsing () throws IOException {
        List<String> urls = parserLotService.prepareURI(START_DATE, END_DATE);
        parserLotService.parsering(urls);
    }
}
