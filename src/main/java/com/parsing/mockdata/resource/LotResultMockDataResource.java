package com.parsing.mockdata.resource;

import com.parsing.mockdata.rozetka_parsing_item.LotResultMockDataGenerator;
import com.parsing.model.LotResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rozetka-parser/generator/lot-result")
@RequiredArgsConstructor
public class LotResultMockDataResource {

    private final LotResultMockDataGenerator lotResultMockDataGenerator;

    @GetMapping
    public List<LotResult> generate() {
        return lotResultMockDataGenerator.generate();
    }
}
