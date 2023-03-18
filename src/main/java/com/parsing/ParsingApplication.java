package com.parsing;

import com.parsing.parsers.parserLot.ParserLotService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import java.io.IOException;
@Service
@SpringBootApplication
@RequiredArgsConstructor
public class ParsingApplication {

    private final ParserLotService parserLotService;

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ParsingApplication.class, args);

    }
}