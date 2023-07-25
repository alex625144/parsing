package com.parsing.service;

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
        StringBuilder lotInformationBuilder = new StringBuilder();
        if (model != null && price != null && amount != null) {
            LaptopItem laptopItem = new LaptopItem();
            laptopItem.setModel(model);
            laptopItem.setPrice(price);
            laptopItem.setAmount(amount);
            laptopItemRepository.save(laptopItem);
        } else {
            if (model == null) {
                lotInformationBuilder.append("Model is null.\n");
            } else {
                lotInformationBuilder.append("model = ").append(model).append("\n");
            }
            if (price == null) {
                lotInformationBuilder.append("Price is null.\n");
            } else {
                lotInformationBuilder.append("price = ").append(price).append("\n");
            }
            if (amount == null) {
                lotInformationBuilder.append("Amount is null\n");
            } else {
                lotInformationBuilder.append("amount = ").append(amount).append("\n");
            }
            log.debug(lotInformationBuilder.toString());
        }
    }
}
