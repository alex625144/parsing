package com.parsing.mockdata.for_manual_testing_rozetkz_parsing_item;

import com.parsing.model.LotPDFResult;
import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.repository.LotPDFResultRepository;
import com.parsing.repository.LotResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LotPDFResultMockDataGenerator {

    private final LotResultRepository lotResultRepository;
    private final LotPDFResultRepository lotPDFResultRepository;
    private final LotResultMockDataGenerator lotResultMockDataGenerator;
    private final LaptopItemMockDataGenerator laptopItemMockDataGenerator;

    @Transactional
    public List<LotPDFResult> generate() {
        List<LotResult> lotResults = new ArrayList<>();
        List<LotPDFResult> lotPDFResults = new ArrayList<>();

        while (lotResults.size() < 6) {
            lotResultMockDataGenerator.generate();
            lotResults = lotResultRepository.findAllByStatusAndLotPDFResultIsNull(Status.PDF_SUCCESSFULL);
        }

        for (LotResult lotResult : lotResults) {
            LotPDFResult lotPDFResult = new LotPDFResult();
            lotPDFResult.setParsingDate(LocalDate.now());
            lotPDFResult.setLotResult(lotResult);
            lotPDFResult.setLaptopItems(laptopItemMockDataGenerator.generate(lotPDFResult));
            lotResult.setLotPDFResult(lotPDFResult);

            lotPDFResults.add(lotPDFResult);
        }
        lotPDFResultRepository.saveAll(lotPDFResults);

        return lotPDFResults;
    }
}
