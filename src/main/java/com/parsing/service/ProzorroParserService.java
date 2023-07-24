package com.parsing.service;

import com.parsing.model.LotPDFResult;
import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.parsers.prozorro.URLListBuilder;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProzorroParserService {

    private static final String NOT_PRESENT = "NOT PRESENT";
    private static final String DK_LAPTOPS_1 = "30230000";
    private static final String DK_LAPTOPS_2 = "30210000";
    private static final String TENDER_NOT_FOUND = "Тендер не знайдено | ProZorro";
    private static int MAX_COUNT = 500;
    private static int TIMEOUT_RECONNECT = 100000;
    private static final List<List<String>> urls = URLListBuilder.buildListURLS();
    private static final List<String> LAPTOPS_DK = List.of(DK_LAPTOPS_1, DK_LAPTOPS_2);
    private final LotResultRepository lotResultRepository;

    public void parse() {
        for (List<String> day : urls) {
            MAX_COUNT = 500;
            for (int x = 0; x < day.size(); x++) {
                log.debug("Starting parsing for url " + day.get(x));
                Document document = null;
                int counter = 1;
                document = getDocument(day.get(x), document, counter);
                Element element = getElement(document);
                if (isTenderNotFound(document) < 1) {
                    x = day.size();
                } else if (Objects.nonNull(element)) {
                    LotResult lot = new LotResult();
                    String Dk = parseDK(document);
                    if (Dk != null && LAPTOPS_DK.contains(Dk)) {
                        lot.setDk(parseDK(document));
                        lot.setLotURL(day.get(x));
                        lot.setPdfURL(parsePDFLink(document));
                        Status status = parsePDFLink(document).equals(NOT_PRESENT) ? Status.CREATED : Status.PARSED;
                        lot.setStatus(status);
                        lot.setLotTotalPrice(parsePrice(document));
                        lot.setParsingDate(parseDate(day.get(x)));
                        lot.setLotPDFResult(new LotPDFResult());
                        saveLotResult(day.get(x), lot);
                    }
                }

            }

        }
    }

    @Transactional
    public void saveLotResult(String url, LotResult lot) {
        lotResultRepository.saveAndFlush(lot);
        log.debug("parsed URL = " + url);
    }

    private Element getElement(Document document) {
        Element element = null;
        try {
            element = document.getElementsByClass("infobox-link").first();
        } catch (Exception e) {
            log.warn("Document doesn't have class \"infobox-link\"");
            e.printStackTrace();
        }
        return element;
    }

    Document getDocument(String url, Document document, int maxReconnect) {
        try {
            document = Jsoup.connect(url)
                    .timeout(TIMEOUT_RECONNECT)
                    .ignoreHttpErrors(true)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            reconnect(document, url);
            log.debug("Reconnect invoked!");
        }
        return document;
    }

    private Document reconnect(Document document, String url) {
        int count = 1;
        int maxCount = 5;
        while (count < maxCount) {
            try {
                document = Jsoup.connect(url)
                        .timeout(TIMEOUT_RECONNECT)
                        .ignoreHttpErrors(true)
                        .get();
                return document;
            } catch (IOException e) {
                e.printStackTrace();
                count++;
                log.debug("Reconnect!");
            }
        }
        return null;
    }

    private String parseDK(Document document) {
        String source = document.getElementsByClass("tender-date padding-left-more").toString();
        Pattern pattern = Pattern.compile("\\d{8}");
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String parsePDFLink(Document document) {
        Optional<Element> element = Optional.ofNullable(document.select("table[class=table table-striped margin-bottom prev]").last());
        if (!element.isPresent()) {
            element = Optional.ofNullable(document.select("table[class=table table-striped margin-bottom prev]").first());
        }
        if (element.isPresent()) {
            List<Element> elements = element.get().getElementsByAttribute("href");
            if (elements.size() > 1) {
                Element source = elements.get(elements.size() - 1);
                return source.attr("href");
            } else if (elements.size() == 1) {
                Element source = elements.get(0);
                return source.attr("href");
            } else {
                return NOT_PRESENT;
            }
        }
        return NOT_PRESENT;
    }

    private LocalDate parseDate(String uri) {
        String date = uri.replaceAll("https://prozorro.gov.ua/tender/UA-", "").substring(0, 10);
        return LocalDate.parse(date);
    }

    private BigDecimal parsePrice(Document document) {
        Optional<Element> element = Optional.ofNullable(document.getElementsByClass("green tender--description--cost--number").first());
        if (element.isPresent()) {
            String source = element.get().getElementsByTag("strong").toString();
            String price = source.replace("<strong>", "")
                    .replace("<span class=\"small\">UAH</span></strong>", "")
                    .replace(" ", "")
                    .replace(",", ".");
            return new BigDecimal(price);
        }
        return BigDecimal.valueOf(0);
    }

    private int isTenderNotFound(Document document) {
        Optional<String> element = Optional.ofNullable(document.title());
        if (element.get().equals(TENDER_NOT_FOUND)) {
            log.debug("Element" + element.get());
            MAX_COUNT--;
        }
        return MAX_COUNT;
    }
}
