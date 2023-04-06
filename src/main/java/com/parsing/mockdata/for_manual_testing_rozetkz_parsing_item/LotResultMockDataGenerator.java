package com.parsing.mockdata.for_manual_testing_rozetkz_parsing_item;

import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class LotResultMockDataGenerator {

    private final LotResultRepository lotResultRepository;
    private final int LOT_RESULT_QUANTITY = 10;
    private final String URL_TEMPLATE = "https://parsing.com/";
    private final String DK = "009023";
    private int lotCounter = 0;

    @Transactional
    public List<LotResult> generate() {
        List<LotResult> lotResults = new ArrayList<>();

        for(int i = 0; i < LOT_RESULT_QUANTITY; i++) {
            LotResult lotResult = new LotResult();

            lotResult.setDk(DK);
            lotResult.setUrl(URL_TEMPLATE + "/lot" + lotCounter++);
            lotResult.setPdfLink(URL_TEMPLATE + "/pdf" + lotCounter);
            lotResult.setStatus(randomStatusGenerator());
            lotResult.setParsingDate(LocalDate.now());

            if (lotResult.getStatus() == Status.PDF_SUCCESSFULL)
                lotResult.setPrice(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(50_000, 1_000_000)));

            lotResults.add(lotResult);
        }

        return lotResultRepository.saveAll(lotResults);
    }

    private Status randomStatusGenerator() {
        int minValue = 0;
        int maxValue = 8;

        return switch (ThreadLocalRandom.current().nextInt(minValue, maxValue)) {
            case 0 -> Status.CREATED;
            case 1 -> Status.PARSED;
            case 2 -> Status.PROCESSED;
            case 3 -> Status.DOWNLOADED;
            case 4 -> Status.PDF_SUCCESSFULL;
            case 5 -> Status.PDF_FAILED;
            case 6 -> Status.ROZETKA_SUCCESSFULL;
            case 7 -> Status.ROZETKA_FAILED;
            default ->
                    throw new IllegalStateException("Unexpected value: " + ThreadLocalRandom.current().nextInt(minValue, maxValue));
        };
    }
}
