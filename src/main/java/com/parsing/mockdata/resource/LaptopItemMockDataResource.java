package com.parsing.mockdata.resource;

import com.parsing.mockdata.mock_data_generator.LaptopItemMockDataGenerator;
import com.parsing.model.LaptopItem;
import com.parsing.model.LotPDFResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/mock_data_generator/laptop-item")
public class LaptopItemMockDataResource {

    private final LaptopItemMockDataGenerator laptopItemMockDataGenerator;

    @PostMapping
    public List<LaptopItem> generate(@RequestBody LotPDFResult lotPDFResult) {
        return laptopItemMockDataGenerator.generate(lotPDFResult);
    }
}
