package com.parsing.pdf.parsing;

import com.parsing.model.LotPDFResult;
import com.parsing.pdf.parsing.modelParsing.Column;
import com.parsing.pdf.parsing.modelParsing.Row;
import com.parsing.repository.LotPDFResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Recognizer {

    private final LotPDFResultRepository lotPDFResultRepository;

    public final void recognizeLotPDFResult(List<Row> rows) {
        for (Row row : rows) {
            LotPDFResult lotPDFResult = new LotPDFResult();
            for (Column column : row.getColumns()) {
                if (column.getParsingResult().contains("Lenovo")) {
                    lotPDFResult.setModel(column.getParsingResult());
                }
                if (column.getParsingResult().contains("13")) {
                    lotPDFResult.setAmount(13);
                }
                if (column.getParsingResult().contains("22 499,95")) {
                    String result = column.getParsingResult();
                    lotPDFResult.setPrice(new BigDecimal(result));
                }
                lotPDFResultRepository.save(lotPDFResult);
            }
        }
    }
}
