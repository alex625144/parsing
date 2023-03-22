package com.parsing.rozetka;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RozetkaParser {

    private final String URL = "https://rozetka.com.ua/search/";

    private final String SEARCH_PARAM = "?text=";



    @SneakyThrows
    public String searchPriceByModel(String model) {
        Document document = Jsoup.connect(httpBuilder(model)).get();
        String source = document.getElementById("rz-client-state").toString();

        //TODO: check for an answer without a model

        String price = "";
        String[] splitedSours = source.split(";price&q;:");
        if (splitedSours.length < 2) return price;
        price = splitedSours[1].split(",")[0];

        return price;
    }



    private String httpBuilder(String text) {
        return URL + SEARCH_PARAM+text;
    }
}
