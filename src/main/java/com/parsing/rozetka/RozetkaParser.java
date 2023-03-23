package com.parsing.rozetka;

import com.parsing.model.LaptopItem;
import com.parsing.model.RozetkaParsingReport;
import com.parsing.model.Status;
import com.parsing.repository.LotRepository;
import com.parsing.repository.RozetkaParsingResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RozetkaParser {

    private final String URL = "https://rozetka.com.ua/search/";
    private final String SEARCH_PARAM = "?text=";

    private final Long TEN_MINUTES = 600000L;

    private final LotRepository lotRepository;
    private final RozetkaParsingResultRepository rozetkaParsingResultRepository;

    @Scheduled(initialDelay = TEN_MINUTES, fixedDelay = TEN_MINUTES)
    public void scheduledParsing() {
        var lots = lotRepository.findAllByStatus(Status.PDF_SUCCESSFULL);
    }

    @SneakyThrows
    public String searchPriceByModel(String model) {
        Document document = Jsoup.connect(httpBuilder(model)).get();
        String source = document.getElementById("rz-client-state").toString();

        String price = "";
        String[] splitedSours = source.split(";price&q;:");
        if (splitedSours.length < 2) return price;
        price = splitedSours[1].split(",")[0];

        return price;
    }

    private List<String> getModelsFromLotResult(List<LaptopItem> items) {
        return items.stream()
                .map(LaptopItem::getModel)
                .collect(Collectors.toList());
    }

    public RozetkaParsingReport savePriceToResultReport(RozetkaParsingReport parsingReport) {
        lotRepository.save(parsingReport.getLotResult());
        return rozetkaParsingResultRepository.save(parsingReport);
    }

    private String httpBuilder(String text) {
        return URL + SEARCH_PARAM+text;
    }
}
