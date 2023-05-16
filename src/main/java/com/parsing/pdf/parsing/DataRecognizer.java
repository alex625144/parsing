package com.parsing.pdf.parsing;

import com.parsing.pdf.parsing.model.Column;
import com.parsing.pdf.parsing.model.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class DataRecognizer {

    private final LotPDFResultService lotPDFResultService;
    private String model = null;
    private Integer amount = 0;
    private BigDecimal price = null;
    private BigDecimal totalPrice = null;

    @Value("${laptop.models}")
    private final List<String> laptopModels;

    public final void recognizeLotPDFResult(List<Row> rows) {
        for (Row row : rows) {
            if (isModelRow(row)) {
                int modelColumnNumber = 0;
                for (Column column : row.getColumns()) {
                    if (isModel(column.getParsingResult())) {
                        model = findModel(column.getParsingResult());
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
                if (amounts.size() == 1) {
                    amount = Integer.parseInt(amounts.get(0));
                } else if(amounts.size() > 1) {
                    log.info("more than one amount number found in one row");
                    amount = 0;
                }

                List<BigDecimal> bigDecimals = prices.stream().map(BigDecimal::new).toList();
                Optional<BigDecimal> min = bigDecimals.stream().min(Comparator.naturalOrder());
                Optional<BigDecimal> max = bigDecimals.stream().max(Comparator.naturalOrder());
                if (min.isPresent() && max.isPresent()) {
                    price = min.get();
                    totalPrice = max.get();
                }
                if (amount == 0 && price != null) {
                    amount = totalPrice.divide(price).toBigInteger().intValueExact();
                }
                if (totalPrice != null && totalPrice.equals(BigDecimal.valueOf(amount).multiply(price))) {
                    lotPDFResultService.saveLaptopItem(model, price, amount);
                }
            }
        }
    }

    private boolean isModelRow(Row row) {
        List<String> listLowerCaseColumn = row.getColumns().stream().map(x -> x.getParsingResult().toLowerCase(Locale.ROOT)).toList();
        for (String column : listLowerCaseColumn) {
            for (String laptopModel : laptopModels) {
                if (column.contains(laptopModel)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String findModel(String input) {
        for (String laptop : laptopModels) {
            if (input.toLowerCase(Locale.ROOT).contains(laptop)) {
                return input;
            }
        }
        return null;
    }

    private boolean isModel(String input) {
        for (String laptop : laptopModels) {
            if (input.toLowerCase(Locale.ROOT).contains(laptop)) {
                return true;
            }
        }
        return false;
    }

    private String findPrices(String input) {
        String result = null;
        if (input.contains(",")) {
            result = input.replace(" ", "").replace("\n", "").replace(",", ".").replace("І", "1");
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

