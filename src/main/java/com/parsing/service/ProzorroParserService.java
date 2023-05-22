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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ProzorroParserService {

    private static final String NOT_PRESENT = "NOT PRESENT";
    private static final List<String> urls = URLListBuilder.buildListURLS();
    private final LotResultRepository lotResultRepository;
    private static final List<String> LAPTOPS_DK = List.of("30230000", "30210000");

    @Transactional
    public void parse() throws IOException {
        for (String url : urls) {
            log.info("Starting parsing for url " + url);
            Document document = Jsoup.connect(url).get();
            Element element = document.getElementsByClass("infobox-link").first();
            if (Objects.nonNull(element)) {
                LotResult lot = new LotResult();
                String Dk = parseDK(document);
                if (Dk != null && LAPTOPS_DK.contains(Dk)) {
                    lot.setDk(parseDK(document));
                    lot.setUrl(url);
                    lot.setPdfLink(parsePDFLink(document));
                    Status status = parsePDFLink(document).equals(NOT_PRESENT) ? Status.CREATED : Status.PARSED;
                    lot.setStatus(status);
                    lot.setPrice(parsePrice(document));
                    lot.setParsingDate(parseDate(url));
                    lot.setLotPDFResult(new LotPDFResult());
                    lotResultRepository.saveAndFlush(lot);
                    UUID id = lot.getId();
                    log.info("parsed URL = " + url);
                }
            }
        }
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
}
