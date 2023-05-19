package com.parsing.parsers.pdf.parsing;

import com.parsing.model.LaptopItem;
import com.parsing.repository.LaptopItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class LotPDFResultService {

    private final LaptopItemRepository laptopItemRepository;

    public void saveLaptopItem(String model, BigDecimal price, Integer amount) {
        StringBuilder logInformation = new StringBuilder();
        if (model != null && price != null && amount != null) {
            LaptopItem laptopItem = new LaptopItem();
            laptopItem.setModel(model);
            laptopItem.setPrice(price);
            laptopItem.setAmount(amount);
            laptopItemRepository.save(laptopItem);
        } else if (model == null) {
            logInformation.append("Model is null.\n");
        } else if (price == null) {
            logInformation.append("Price is null.\n");
        } else {
            logInformation.append("Amount is null\n");
        }
        log.info(logInformation.toString());
    }
}
