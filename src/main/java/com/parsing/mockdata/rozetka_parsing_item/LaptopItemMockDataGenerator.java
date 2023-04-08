package com.parsing.mockdata.rozetka_parsing_item;

import com.parsing.model.LaptopItem;
import com.parsing.model.LotPDFResult;
import com.parsing.repository.LaptopItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class LaptopItemMockDataGenerator {

    private final LaptopItemRepository laptopItemRepository;
    private int modelCounter = 0;
    private static final List<String> PREPARE_MOCK_MODEL_LIST = List.of(
            "Acer Aspire 7 A715-42G-R3EZ", "Dell Vostro 15 3501", "Xiaomi Mi RedmiBook 15",
            "Apple MacBook Air 13\" M1 256GB 2020", "Lenovo IdeaPad 3 15IAU7", "ASUS Laptop X515EA-BQ2066",
            "Acer Aspire 3 A315-58G-548E", "ASUS TUF Gaming F15 FX506LHB-HN323", "Microsoft Surface Laptop 5",
            "Acer Aspire 3 A315-58G-3953", "HP Pavilion 15-eh2234nw", "Acer Nitro 5 AN515-57",
            "Lenovo IdeaPad L3 15ITL6", "Lenovo IdeaPad Gaming 3 15ACH6", "Asus ROG Strix G15 G513IC-HN092",
            "Huawei MateBook 14S 14.2\"", "Samsung Galaxy Book 2 Pro", "Huawei MateBook D 16",
            "NOT_VALID_MODEL_NAME", "NOT_VALID_MODEL_NAME", "NOT_VALID_MODEL_NAME",
            "NOT_VALID_MODEL_NAME", "NOT_VALID_MODEL_NAME", "NOT_VALID_MODEL_NAME",
            "NOT_VALID_MODEL_NAME", "NOT_VALID_MODEL_NAME", "NOT_VALID_MODEL_NAME",
            "NOT_VALID_MODEL_NAME", "NOT_VALID_MODEL_NAME", "NOT_VALID_MODEL_NAME"
    );

    @Transactional
    public List<LaptopItem> generate(LotPDFResult pdfResult) {
        if (pdfResult.getLaptopItems() != null ) return  pdfResult.getLaptopItems();

        List<LaptopItem> laptopItems = new ArrayList<>();
        int minPriceViolation = 0;
        int maxPriceViolation = 25_000;
        int amount = getAverageAmount(pdfResult);
        int minItems = 1;
        int maxItems = amount + 1;
        int minPrice = 10_000;
        int maxPrice = 100_000;
        int itemQuantity = amount / ThreadLocalRandom.current().nextInt(minItems, maxItems);
        int itemsLeftCounter = amount;
        BigDecimal amountPrice = BigDecimal.valueOf(0);

        for(int i = 0; i <= itemQuantity; i++) {
            LaptopItem laptopItem = new LaptopItem();
            laptopItem.setModel(getModel(modelCounter++));
            laptopItem.setAmount(ThreadLocalRandom.current().nextInt(1, itemsLeftCounter));
            laptopItem.setPrice(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(minPrice, maxPrice)));
            laptopItem.setLotPDFResult(pdfResult);
            if((itemsLeftCounter -= laptopItem.getAmount()) < 2) itemsLeftCounter = 2;

            amountPrice = amountPrice.add(laptopItem.getPrice().multiply(BigDecimal.valueOf(laptopItem.getAmount())));
            laptopItems.add(laptopItem);
        }

        pdfResult.getLotResult().setPrice(amountPrice.add(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(minPriceViolation, maxPriceViolation))));
        laptopItemRepository.saveAll(laptopItems);
        return laptopItems;
    }

    private int getAverageAmount(LotPDFResult pdfResult) {
        return pdfResult.getLotResult().getPrice()
                .divide(BigDecimal.valueOf(25_000L), 2, RoundingMode.HALF_UP)
                .intValue();
    }


    private String getModel(int index) {
        if(index >= PREPARE_MOCK_MODEL_LIST.size()) {
            index %= PREPARE_MOCK_MODEL_LIST.size();
        }

        return PREPARE_MOCK_MODEL_LIST.get(index);
    }
}
