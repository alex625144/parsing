package com.parsing.rozetka;

import com.parsing.model.LotResult;
import com.parsing.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RozetkaParserSevice {

    private final RozetkaParser rozetkaParser;

    public String findPriceByURL(String laptop) {
        return rozetkaParser.searchPriceByModel(laptop);
    }

}
