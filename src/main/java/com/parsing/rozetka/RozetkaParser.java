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
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RozetkaParser {

    private static final String ROZETKA_URL = "https://rozetka.com.ua/";
    private static final String SEARCH = "search/";
    private static final String SEARCH_PARAM = "?text=";

    private final LotResultRepository lotResultRepository;
    private final RozetkaParsingResultRepository rozetkaParsingResultRepository;

    public List<RozetkaParsingReport> parsingByLotResult(LotResult lotResult) {
        List<String> models = getModelsFromLotResult(lotResult);
        List<RozetkaParsingReport> result = new ArrayList<>();

        boolean isRozetkaParsingSuccessful = false;
        for (String model : models) {
            BigDecimal prise = searchPriceByModel(model);
            if (prise == null || prise.doubleValue() == 0.0 ) continue;

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
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        ChromeOptions options= new ChromeOptions();
        options.setHeadless(true);
        WebDriver driver = new ChromeDriver();
        driver.get("https://rozetka.com.ua/search/?text=Lenovo+IdeaPad+Gaming+3+15ACH6&producer=lenovo&page=1");

        Document document = Jsoup.parse(driver.getPageSource());

        String price = "";
        String source = document.toString();
        String[] splitedSours = source.split(";price&q;:");
        if (splitedSours.length < 2) return BigDecimal.ZERO;
        price = splitedSours[1].split(",")[0];

        if (!price.matches("^[-+]?\\d+(\\.\\d+)?$")) {
            price = searchPriceByRozetkaModelId(document, model);
        }

        return BigDecimal.valueOf(Double.valueOf(price));
    }

    @SneakyThrows
    private String searchPriceByRozetkaModelId(Document document, String model) {
        String price = "";
        Element script = document.selectFirst("script#rz-client-state");
        String source = script.html();
        String[] splitedSours = source.split("q;,&q;id&q;:");
        String modelId = splitedSours[1].split(",")[0];
        if (!modelId.matches("\\d{9}")) return  "0";

        Document modelDocument = Jsoup.connect(httpBuilderWithModelId(model, modelId)).get();
        String newSource = modelDocument.toString();
        String[] splitedSource = newSource.split(",\"price\":\"");
        price = splitedSource[1].split(",")[0];

        return price;
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
        return ROZETKA_URL + SEARCH + SEARCH_PARAM + text + "&section_id=80004";
    }

    private String httpBuilderWithModelId(String model, String modelId1) {
        model = model.toLowerCase();
        model = modelId1.replace(" ", "_");

        return ROZETKA_URL + "/" + model + "/p" + modelId1;
    }
}
