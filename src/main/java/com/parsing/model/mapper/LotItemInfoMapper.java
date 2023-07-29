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
public interface LotItemInfoMapper {

    @Named("toLotItemInfoList")
    @IterableMapping(qualifiedByName = "toLotItemInfo")
    List<LotItemInfo> toLotItemInfoList(List<LaptopItem> laptopItems);

    @Named("toLotItemInfo")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalItemPrice", expression = "java(calculateTotalPrice(laptopItem))")
    LotItemInfo toLotItemInfo(LaptopItem laptopItem);

    default BigDecimal calculateTotalPrice(LaptopItem laptopItem) {
        return laptopItem.getPrice()
                .multiply(BigDecimal.valueOf(laptopItem.getAmount()));
    }
}
