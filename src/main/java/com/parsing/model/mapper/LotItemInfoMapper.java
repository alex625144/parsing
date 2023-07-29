package com.parsing.model.mapper;

import com.parsing.model.LaptopItem;
import com.parsing.model.LotItemInfo;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class LotItemInfoMapper {

    @IterableMapping(qualifiedByName = "toLotItemInfoList")
    public abstract List<LotItemInfo> toLotItemInfoList(List<LaptopItem> laptopItems);

    @Named("toLotItemInfoList")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalItemPrice", source = "laptopItem", qualifiedByName = "calculateTotalPrice")
    public abstract LotItemInfo toLotItemInfoList(LaptopItem laptopItem);

    @Named("calculateTotalPrice")
    public BigDecimal calculateTotalPrice(LaptopItem laptopItem) {
        return laptopItem.getPrice()
                .multiply(BigDecimal.valueOf(laptopItem.getAmount()));
    }
}
