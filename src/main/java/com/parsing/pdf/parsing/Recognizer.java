package com.parsing.pdf.parsing;

import com.parsing.pdf.parsing.modelParsing.Column;
import com.parsing.pdf.parsing.modelParsing.Row;
import com.parsing.repository.LotPDFResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@RequiredArgsConstructor
@Slf4j
public class Recognizer {

    private static final List<String> LAPTOP_MODELS = List.of("lenovo", "asus", "acer");

    private final LotPDFResultRepository lotPDFResultRepository;
    private final SaverLotPDFResult saverLotPDFResult;


    public final void recognizeLotPDFResult(List<Row> rows) {
        for (Row row : rows) {
            String model = null;
            int amount = 0;
            BigDecimal price = null;
            BigDecimal totalprice = null;

            if (isModelRow(row)) {
                int modelColumnNumber = 0;
                for (Column column : row.getColumns()) {
                    model = findModel(column.getParsingResult());
                    if (model != null) {
                        modelColumnNumber = row.getColumns().indexOf(column);
                    }
                }
                List<String> amounts = new ArrayList<>();
                List<String> prices = new ArrayList<>();

                for (int x = modelColumnNumber; x < row.getColumns().size(); x++) {
                    if (findAmount(row.getColumns().get(x).getParsingResult()) != null) {
                        amounts.add(findAmount(row.getColumns().get(x).getParsingResult()));
                    }
                    if (findPrices(row.getColumns().get(x).getParsingResult()) != null) {
                        prices.add(findPrices(row.getColumns().get(x).getParsingResult()));
                    }
                }
                if (amounts.size() > 1) {
                    log.info("more than one amount number found in one row"); //todo implement as a part of feedback system
                    amount = Integer.parseInt(amounts.get(0));
                }

                List<BigDecimal> bigDecimals = prices.stream().map(BigDecimal::new).toList();
                Optional<BigDecimal> min = bigDecimals.stream().min(Comparator.naturalOrder());
                if (min.isPresent()) {
                    price = min.get();
                }
                Optional<BigDecimal> max = bigDecimals.stream().max(Comparator.naturalOrder());
                if (max.isPresent()) {
                    totalprice = max.get();
                }
                if (totalprice!=null && totalprice.equals(BigDecimal.valueOf(amount).multiply(price))) {
                    saverLotPDFResult.saveLaptopItem(model, price, amount);
                }
            }
        }
    }

    private boolean isModelRow(Row row) {
        List<String> listLowerCaseColumn = row.getColumns().stream().map(x -> x.getParsingResult().toLowerCase(Locale.ROOT)).toList();
        for (String column : listLowerCaseColumn) {
            for (String laptopModel : LAPTOP_MODELS) {
                if (column.contains(laptopModel)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String findModel(String input) {
        for (String model : LAPTOP_MODELS) {
            if (input.toLowerCase(Locale.ROOT).contains(model)) {
                return input;
            }
        }
        return null;
    }

    private String findPrices(String input) {
        String result = null;
        if (input.contains(",")) {
            result = input.replace(" ", "");
        }
        return result;
    }

    private String findAmount(String input) {
        if (input.length() < 4 && input.length() > 1) {
            String amountPattern = "\\d{1,2}\\b";
            Pattern pattern = Pattern.compile(amountPattern);
            Matcher matcher = pattern.matcher(input);
            String result = null;
            if (matcher.find()) {
                result = matcher.group();
                log.info("Matched amount found: " + result);
            } else {
                log.info("No match for amount found");
            }
            return result;
        }
        return null;
    }
}

