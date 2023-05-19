package com.parsing.parsers.rozetka;

import com.parsing.model.LaptopItem;
import com.parsing.model.LotResult;
import com.parsing.model.RozetkaParsingReport;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import com.parsing.repository.RozetkaParsingResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RozetkaParser {

    private static final String ROZETKA_URL = "https://rozetka.com.ua";
    private static final String SEARCH = "/search";
    private static final String SEARCH_PARAM = "/?text=";
    private static final String LAPTOP_SECTION_ID = "&section_id=80004";

    private final LotResultRepository lotResultRepository;
    private final RozetkaParsingResultRepository rozetkaParsingResultRepository;
    private final WebDriver driver;

    public List<RozetkaParsingReport> parseByLotResult(LotResult lotResult) {
        List<String> models = getModelsFromLotResult(lotResult);
        List<RozetkaParsingReport> result = new ArrayList<>();

        boolean isRozetkaParsingSuccessful = false;
        for (String model : models) {
            BigDecimal price = searchPriceByModel(model);
            if (price == null || price.doubleValue() == 0.0) continue;

            isRozetkaParsingSuccessful = true;
            RozetkaParsingReport rozetkaParsingReport = new RozetkaParsingReport();
            rozetkaParsingReport.setModel(model);
            rozetkaParsingReport.setSearchURL(httpBuilder(model));
            rozetkaParsingReport.setMarketPrice(price);
            rozetkaParsingReport.setLotResult(lotResult);

            result.add(savePriceToResultReport(rozetkaParsingReport));
        }

        lotResult.setStatus(isRozetkaParsingSuccessful ? Status.ROZETKA_SUCCESSFULL : Status.ROZETKA_FAILED);
        lotResultRepository.save(lotResult);

        return result;
    }

    @SneakyThrows
    public BigDecimal searchPriceByModel(String model) {
        String price = "0";

        driver.get(httpBuilder(model));
        Document document = Jsoup.parse(driver.getPageSource());
        Elements elements = document.getElementsByClass("goods-tile__price-value");

        if (elements.size() != 0) {
            price = elements.get(0).text().replace("₴", "").replace(" ", "");
        }

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
        text = text.replaceAll(" ", "+");
        return ROZETKA_URL + SEARCH + SEARCH_PARAM + text + LAPTOP_SECTION_ID;
    }
}
