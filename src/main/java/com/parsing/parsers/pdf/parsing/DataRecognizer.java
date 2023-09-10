package com.parsing.parsers.pdf.parsing;

import com.parsing.exception.RecognizeLotPDFResultException;
import com.parsing.exception.UnableToConvertPriceException;
import com.parsing.parsers.pdf.parsing.model.Column;
import com.parsing.parsers.pdf.parsing.model.Row;
import com.parsing.service.LotPDFResultService;
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

    @Value("${laptop.models}")
    private String[] laptopModels;

    private static final int MIN_PRICE_AMOUNT_DIGITS = 4;
    private String model;
    private Integer amount = 0;
    private BigDecimal price = null;
    private BigDecimal totalPrice = null;

    public final boolean recognizeLotPDFResult(List<Row> rows) {
        log.debug("Class DataRecognizer started.");
        boolean result = false;
        try {
            for (Row row : rows) {
                if (isModelRow(row)) {
                    int modelColumnNumber = getModelColumnNumber(row);
                    List<String> amounts = new ArrayList<>();
                    List<String> prices = new ArrayList<>();
                    for (int x = modelColumnNumber; x < row.getColumns().size(); x++) {
                        String amountTemp = findAmount(row.getColumns().get(x).getParsingResult());
                        if (amountTemp != null) {
                            amounts.add(amountTemp);
                        }
                        String pricesTemp = findPrices(row.getColumns().get(x).getParsingResult());
                        if (pricesTemp != null) {
                            prices.add(pricesTemp);
                        }
                    }
                    getAmount(amounts);
                    getPriceAndTotalPrice(prices);
                    result = saveItems();
                }
            }
            log.debug("Class DataRecognizer finished.");
            return result;
        }
        catch (Exception e) {
            throw new RecognizeLotPDFResultException(e);
        }
    }

    private boolean saveItems() {
        if (isItemsExist()) {
            lotPDFResultService.saveLaptopItem(model, price, amount);
            return true;
        }
        return false;
    }

    private boolean isItemsExist() {
        return totalPrice != null && totalPrice.equals(BigDecimal.valueOf(amount).multiply(price));
    }


    private void getAmount(List<String> amounts) {
        if (amounts.size() == 1) {
            amount = Integer.parseInt(amounts.get(0));
        } else if (amounts.size() > 1) {
            log.debug("more than one amount number found in one row");
            amount = null;
        }
        if (amount == null && price != null) {
            amount = totalPrice.divide(price).toBigInteger().intValueExact();
        }
    }

    private void getPriceAndTotalPrice(List<String> prices) {
        List<BigDecimal> bigDecimals = null;
        try {
            bigDecimals = prices.stream().map(BigDecimal::new).toList();
        } catch (UnableToConvertPriceException exception) {
            throw new UnableToConvertPriceException("Price doesn't not converted");
        }
        Optional<BigDecimal> min = bigDecimals.stream().min(Comparator.naturalOrder());
        Optional<BigDecimal> max = bigDecimals.stream().max(Comparator.naturalOrder());
        if (min.isPresent() && max.isPresent()) {
            price = min.get();
            totalPrice = max.get();
        }
    }

    private int getModelColumnNumber(Row row) {
        int modelColumnNumber = 0;
        for (Column column : row.getColumns()) {
            if (findModel(column.getParsingResult()) != null) {
                model = findModel(column.getParsingResult());
                modelColumnNumber = row.getColumns().indexOf(column);
            }
        }
        return modelColumnNumber;
    }

    private boolean isModelRow(Row row) {
        List<String> listLowerCaseColumn = row.getColumns().stream()
                .map(x -> x.getParsingResult().toLowerCase(Locale.ROOT)).toList();
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

    private String findPrices(String input) {
        String result = null;
        if (input.contains(",")) {
            result = input.replace(" ", "").replace("\n", "").replace(",", ".").replace("І", "1");
        }
        return result;
    }

    private String findAmount(String input) {
        if (input.length() < MIN_PRICE_AMOUNT_DIGITS && input.length() > 1) {
            String amountPattern = "\\d{1,2}\\b";
            Pattern pattern = Pattern.compile(amountPattern);
            Matcher matcher = pattern.matcher(input);
            String result = null;
            if (matcher.find()) {
                result = matcher.group();
                log.debug("Matched amount found: " + result);
            } else {
                log.debug("No match for amount found");
            }
            return result;
        }
        return null;
    }
}

