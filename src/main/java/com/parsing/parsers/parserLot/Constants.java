package com.parsing.parsers.parserLot;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Constants {
    public final static LocalDate START_DATE = LocalDate.of(2023, 02, 21);
    public final static LocalDate END_DATE = LocalDate.of(2023, 02, 21);
    private final int END = 4000;

    public List<String> createrCounter() {
        List<Integer> listInteger = new ArrayList<>();
        for (int i = 1; i < END; i++) {
            listInteger.add(i);
        }
        List<String> listString = listInteger.stream().map((i) -> i.toString()).collect(Collectors.toList());
        for (int i = 0; i < listString.size(); i++) {
            if (listString.get(i).length() == 1) {
                listString.set(i, "00000" + listString.get(i));
            } else if (listString.get(i).length() == 2) {
                listString.set(i, "0000" + listString.get(i));
            } else if (listString.get(i).length() == 3) {
                listString.set(i, "000" + listString.get(i));
            } else if (listString.get(i).length() == 4) {
                listString.set(i, "00" + listString.get(i));
            } else if (listString.get(i).length() == 5) {
                listString.set(i, "0" + listString.get(i));
            }
        }
        return listString;
    }

    public List<String> createDates(LocalDate START_DATE, LocalDate END_DATE) {
        List<String> resultList = new ArrayList<>();
        Period  days = Period.between(START_DATE, END_DATE);
        int counter = days.getDays();
        if (counter==0) {
            resultList.add(START_DATE.toString());
            return resultList;
        }
        for (Long i = 0L ; i< counter; i++){
            resultList.add(START_DATE.plusDays(i).toString());
        }
        return resultList;
    }
}
