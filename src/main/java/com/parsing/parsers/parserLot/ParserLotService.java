package com.parsing.parsers.parserLot;

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
import java.util.stream.Collectors;

import static com.parsing.parsers.parserLot.Constants.END_DATE;
import static com.parsing.parsers.parserLot.Constants.START_DATE;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ParserLotService {

    private final LotResultRepository lotRepository;

    @Transactional
    public void parse(List<String> urls) throws IOException {
        for (String url : urls) {
            Document document = Jsoup.connect(url).get();
            String source = document.getElementsByClass("tender-date padding-left-more").toString();
            Pattern pattern = Pattern.compile("\\d{8}");
            Matcher matcher = pattern.matcher(source);
            int start = 0;
            int end = 0;
            int count = 0;
            while (matcher.find()) {
                count++;
                start = matcher.start();
                end = matcher.end();

            }
            String dk = source.substring(start,end);
            String uri = url;
            Element elements = document.getElementsByClass("infobox-link").first();
            if (elements != null) {
                Element el = elements.getElementsByTag("a").first();
                String pdfLink = el.attr("href");
                LotResult lot = new LotResult();

                Element elementos = document.getElementsByClass("green tender--description--cost--number").first();
                String element = elementos.getElementsByTag("strong").toString();
                String els = element.replaceAll("<strong>", "");
                String elss = els.replaceAll("<span class=\"small\">UAH</span></strong>", "");
                String elsss = elss.replaceAll(" ", "");
                String elssss = elsss.replaceAll(",", ".");
                BigDecimal price = new BigDecimal(elssss);

                String date = uri.replaceAll("https://prozorro.gov.ua/tender/UA-", "");
                String dates = date.substring(0, 10);
                LocalDate data = LocalDate.parse(dates);
                lot.setDk(dk);
                lot.setParsingDate(data);
                lot.setPdfLink(pdfLink);
                lot.setPrice(price);
                lot.setStatus(Status.PARSED);
                lot.setUrl(uri);
                lotRepository.save(lot);
                log.debug("saved");
            }
        }

    }

    public List<String> prepareURI() {
        Constants constants = new Constants();
        List<String> listDates = constants.createDates(START_DATE, END_DATE);
        List<String> listLots = constants.createrCounter();
        List<String> temp = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        String MAIN_URI = "https://prozorro.gov.ua/tender/UA";
        String DEFIS = "-";
        String END_URI = "-a";
        temp = listDates.stream().map((x) -> MAIN_URI.concat(DEFIS).concat(x).concat(DEFIS)).collect(Collectors.toList());
        for (String datesCounter : temp) {
            for (String intCounter : listLots) {
                urls.add(datesCounter.concat(intCounter).concat(END_URI));
            }
        }
        log.warn("prepare URI complete");
        return urls;
    }
}
