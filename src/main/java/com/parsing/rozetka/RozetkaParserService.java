package com.parsing.rozetka;


import com.parsing.model.*;
import com.parsing.repository.LotResultRepository;
import com.parsing.repository.ResultReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RozetkaParserService {

    private final RozetkaParser rozetkaParser;
    private final LotResultRepository lotResultRepository;
    private final ResultReportRepository resultReportRepository;

    private static final long TEN_MINUTES = 600000L;

    public String findPriceByModel(String model) {
        return rozetkaParser.searchPriceByModel(model).toString();
    }

    @Scheduled(initialDelay = TEN_MINUTES, fixedDelay = TEN_MINUTES)
    public void scheduledParsing(){
        List<LotResult> lotResults = lotResultRepository.findAllByStatus(Status.PDF_SUCCESSFULL);

        for(LotResult lotResult : lotResults) {
            List<RozetkaParsingReport> rozetkaParsingReports = rozetkaParser.parsingByLotResult(lotResult);
            if(lotResult.getStatus() == Status.ROZETKA_FAILED) continue;

            BigDecimal totalPriceViolation = new BigDecimal(0);
            List<ResultReportItem> resultReportItems = new ArrayList<>(rozetkaParsingReports.size());
            ResultReport resultReport = new ResultReport();

            Map<String, LaptopItem> laptopsByModel = lotResult.getLotPDFResult().getLaptopItems().stream()
                    .collect(Collectors.toMap(LaptopItem::getModel, x -> x));
            for(RozetkaParsingReport rozetkaParsingReport : rozetkaParsingReports) {
                LaptopItem laptopItem = laptopsByModel.get(rozetkaParsingReport.getModel());

                ResultReportItem reportItem = new ResultReportItem();
                reportItem.setModel(rozetkaParsingReport.getModel());
                reportItem.setAmount(laptopItem.getAmount());
                reportItem.setItemPrice(laptopItem.getPrice());
                reportItem.setMarketPrice(rozetkaParsingReport.getMarketPrice());
                reportItem.setPriceViolation(reportItem.getItemPrice().subtract(
                        reportItem.getMarketPrice()).multiply(new BigDecimal(reportItem.getAmount())));
                reportItem.setResultReport(resultReport);
                resultReportItems.add(reportItem);

                totalPriceViolation = totalPriceViolation.add(reportItem.getPriceViolation());
            }

            resultReport.setDk(lotResult.getDk());
            resultReport.setProzorroURL(lotResult.getUrl());
            resultReport.setLotPrice(lotResult.getPrice());
            resultReport.setItems(resultReportItems);
            resultReport.setTotalPriceViolation(totalPriceViolation);

            resultReportRepository.save(resultReport);
        }
    }
}
