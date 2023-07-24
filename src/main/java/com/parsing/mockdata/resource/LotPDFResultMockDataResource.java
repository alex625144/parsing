package com.parsing.mockdata.resource;

import com.parsing.mockdata.mock_data_generator.LotPDFResultMockDataGenerator;
import com.parsing.model.LotPDFResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/mock_data_generator/lot-pdf-result")
public class LotPDFResultMockDataResource {

    private final LotPDFResultMockDataGenerator lotPDFResultMockDataGenerator;

    @GetMapping
    public List<LotPDFResult> generate() {
        return lotPDFResultMockDataGenerator.generate();
    }
}
