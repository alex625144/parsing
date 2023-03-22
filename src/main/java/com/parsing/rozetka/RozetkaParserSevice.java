package com.parsing.rozetka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RozetkaParserSevice {

    private final RozetkaParser rozetkaParser;

    public String findPriceByURL(String laptop) {
        return rozetkaParser.searchPriceByModel(laptop);
    }

//    getModelFromResultLot()

//    savePriceToResultReport()
}
