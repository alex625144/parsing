package com.parsing.service;

import com.parsing.api.ExtractorLots;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Service
public class APIParserService {

    private final ExtractorLots extractorLots;

    public void parse() {
        extractorLots.extractLots();
    }
}