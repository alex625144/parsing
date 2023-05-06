package com.parsing.pdf.parsing;

import com.parsing.model.LaptopItem;
import com.parsing.repository.LaptopItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaverLotPDFResult {

    private final LaptopItemRepository laptopItemRepository;

    public boolean saveLaptopItem(String model, BigDecimal price, Integer amount) {
        if (model != null && price != null && amount != null) {
            LaptopItem laptopItem = new LaptopItem();
            laptopItem.setModel(model);
            laptopItem.setPrice(price);
            laptopItem.setAmount(amount);
            laptopItemRepository.save(laptopItem);
            return true;
        }
        return false;
    }
}
