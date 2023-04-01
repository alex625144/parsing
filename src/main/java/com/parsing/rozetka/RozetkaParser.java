package com.parsing.rozetka;

import com.parsing.model.LotResult;
import com.parsing.model.RozetkaParsingReport;
import com.parsing.model.LaptopItem;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import com.parsing.repository.RozetkaParsingResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RozetkaParser {

    private static final String URL = "https://rozetka.com.ua/search/";
    private static final String SEARCH_PARAM = "?text=";


    private final LotResultRepository lotResultRepository;
    private final RozetkaParsingResultRepository rozetkaParsingResultRepository;

    public List<RozetkaParsingReport> parsingByLotResult(LotResult lotResult) {
        List<String> models = getModelsFromLotResult(lotResult);
        List<RozetkaParsingReport> result = new ArrayList<>();

        boolean isRozetkaParsingSuccessful = false;
        for (String model : models) {
            BigDecimal prise = searchPriceByModel(model);
            if (prise.doubleValue() == 0.0) continue;

            isRozetkaParsingSuccessful = true;
            RozetkaParsingReport rozetkaParsingReport = new RozetkaParsingReport();
            rozetkaParsingReport.setModel(model);
            rozetkaParsingReport.setSearchURL(httpBuilder(model));
            rozetkaParsingReport.setMarketPrice(prise);
            rozetkaParsingReport.setLotResult(lotResult);

            result.add(savePriceToResultReport(rozetkaParsingReport));
        }

        lotResult.setStatus(isRozetkaParsingSuccessful ? Status.ROZETKA_SUCCESSFULL : Status.ROZETKA_FAILED);
        lotResultRepository.save(lotResult);

        return result;
    }

    @SneakyThrows
    public BigDecimal searchPriceByModel(String model) {
        Document document = Jsoup.connect(httpBuilder(model)).get();
        String source = document.getElementById("rz-client-state").toString();

        String price = "";
        String[] splitedSours = source.split(";price&q;:");
        if (splitedSours.length < 2) return null;
        price = splitedSours[1].split(",")[0];

        return BigDecimal.valueOf(Double.valueOf(price));
    }

    private List<String> getModelsFromLotResult(LotResult lotResult) {
        return lotResult.getLotPDFResult().getLaptopItems().stream()
                .map(LaptopItem::getModel)
                .collect(Collectors.toList());
    }

    public RozetkaParsingReport savePriceToResultReport(RozetkaParsingReport parsingReport) {
        return rozetkaParsingResultRepository.save(parsingReport);
    }

    private String httpBuilder(String text) {
        return URL + SEARCH_PARAM + text;
    }
}
