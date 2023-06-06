package com.parsing.parsers.prozorro;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class URLListBuilder {

    private static final LocalDate START_DATE = LocalDate.of(2023, 04, 04);
    private static final LocalDate END_DATE = LocalDate.of(2023, 05, 24);
    private static final int START_LOT = 1;
    private static final int END_LOT = 15000;
    private static final String MAIN_URI = "https://prozorro.gov.ua/tender/UA";
    private static final String DASH = "-";
    private static final String END_URI = "-a";


    public static List<List<String>> buildListURLS() {
        List<String> dates = createDates();
        List<List<String>> result = new ArrayList<>();
        for (String date : dates) {
            result.add(buildListUrlsForDay(date));
        }
        log.debug("build list URLS complete for " + result.size());
        return result;
    }

    private static List<String> buildListUrlsForDay(String day) {
        List<String> lots = createDayLots();
        List<String> urlsDay = new ArrayList<>();
        String temp = MAIN_URI.concat(DASH).concat(day).concat(DASH);
        for (String intCounter : lots) {
            urlsDay.add(temp.concat(intCounter).concat(END_URI));
        }
        return urlsDay;
    }

    private static List<String> createDayLots() {
        List<Integer> lots = IntStream.range(START_LOT, END_LOT).boxed().toList();
        List<String> listString = lots.stream().map(Object::toString).toList();
        List<String> result = new ArrayList<>(listString.size());
        for (int i = 0; i < listString.size(); i++) {
            if (listString.get(i).length() == 1) {
                result.add("00000" + listString.get(i));
            } else if (listString.get(i).length() == 2) {
                result.add("0000" + listString.get(i));
            } else if (listString.get(i).length() == 3) {
                result.add("000" + listString.get(i));
            } else if (listString.get(i).length() == 4) {
                result.add("00" + listString.get(i));
            } else if (listString.get(i).length() == 5) {
                result.add("0" + listString.get(i));
            }
        }
        return result;
    }

    private static List<String> createDates() {
        List<String> dates = new ArrayList<>();
        Period days = Period.between(START_DATE, END_DATE);
        int counter = days.getDays();
        if (counter == 0) {
            dates.add(START_DATE.toString());
            return dates;
        }
        for (Long i = 0L; i < counter; i++) {
            dates.add(START_DATE.plusDays(i).toString());
        }
        return dates;
    }
}