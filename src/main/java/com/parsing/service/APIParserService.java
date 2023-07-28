package com.parsing.service;

import com.parsing.api.ExtractorLotId;
import com.parsing.api.ExtractorLotInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Service
public class APIParserService {

    private final ExtractorLotInformation extractorLotInformation;

    private final ExtractorLotId extractorLots;

    public void parse() {
        extractorLots.extractLots();
    }

    public void parseInfo(String lotId) {
        extractorLotInformation.extractLotInformation(lotId);
    }
}