package com.parsing.parsers.parserlot;

import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ParserLotService {

    private final LotResultRepository lotRepository;
    List<String> urls = prepareURI();
    private static final  String NOT_FOUND_HREF = "NOT FOUND HREF";

    @Transactional
    public void parse() throws IOException {
        for (String url : urls) {
            Document document = Jsoup.connect(url).get();
            Element element = document.getElementsByClass("infobox-link").first();
            if (element != null) {
                LotResult lot = new LotResult();
                lot.setUrl(url);
                lot.setDk(parseDK(document));
                lot.setParsingDate(parseDate(url));
                lot.setPdfLink(parsePDFLink(document));
                lot.setPrice(parsePrice(document));
                if (parsePDFLink(document).equals(NOT_FOUND_HREF)) {
                    lot.setStatus(Status.CREATED);
                } else {
                    lot.setStatus(Status.PARSED);
                }
                lotRepository.save(lot);
                log.info("parsed URL = " + url);
            }
        }
    }

    private String parseDK(Document document) {
        String source = document.getElementsByClass("tender-date padding-left-more").toString();
        Pattern pattern = Pattern.compile("\\d{8}");
        Matcher matcher = pattern.matcher(source);
        String dk = null;
        if (matcher.find()) {
            dk = matcher.group();
        }
        return dk;
    }

    private String parsePDFLink(Document document) {
        Element element = document.select("table[class=table table-striped margin-bottom prev]").last();
        if (element == null){
            element = document.select("table[class=table table-striped margin-bottom prev]").first();
        }
        if (element != null) {
            List<Element> elements = element.getElementsByAttribute("href");
            if (elements.size() > 1) {
                Element source = elements.get(elements.size() - 1);
                return source.attr("href");
            } else if (elements.size() == 1) {
                Element source = elements.get(0);
                return source.attr("href");
            } else {
                return NOT_FOUND_HREF;
            }
        }
        return NOT_FOUND_HREF;
    }

    LocalDate parseDate(String uri) {
        String date = uri.replaceAll("https://prozorro.gov.ua/tender/UA-", "").substring(0, 10);
        return LocalDate.parse(date);
    }

    BigDecimal parsePrice(Document document) {
        Element element = document.getElementsByClass("green tender--description--cost--number").first();
        String source = element.getElementsByTag("strong").toString();
        String price = source.replace("<strong>", "")
                .replace("<span class=\"small\">UAH</span></strong>", "")
                .replace(" ", "")
                .replace(",", ".");
        return new BigDecimal(price);
    }

    public List<String> prepareURI() {
        final String MAIN_URI = "https://prozorro.gov.ua/tender/UA";
        final String DASH = "-";
        final String END_URI = "-a";
        Constants constants = new Constants();
        List<String> listDates = constants.createDates();
        List<String> listLots = constants.createCounter();
        List<String> listUrls = new ArrayList<>();
        List<String> temp = listDates.stream().map(x -> MAIN_URI.concat(DASH).concat(x).concat(DASH)).toList();
        for (String datesCounter : temp) {
            for (String intCounter : listLots) {
                listUrls.add(datesCounter.concat(intCounter).concat(END_URI));
            }
        }
        log.debug("prepare URI complete");
        return listUrls;
    }
}
