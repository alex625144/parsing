package com.parsing.service;

import com.parsing.api.LotIdExtractor;
import com.parsing.api.LotInformationExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Service
public class APIParserService {

    private final LotInformationExtractor extractorLotInformation;

    private final LotIdExtractor extractorLots;

    public void parseLotId() {
        extractorLots.tryExtractLots();
    }

    public void parseLotInformation(String lotId) {
        extractorLotInformation.tryExtractLotInformation(lotId);
    }

    public void parseLotInformation() {
        extractorLotInformation.tryExtractAllLotsInformation();
    }
}