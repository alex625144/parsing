package com.parsing.parsers.parserlot.openai.DTO;

import com.parsing.model.LaptopModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LaptopMapper {
    @Mapping(target = "id", ignore = true)
    LaptopModel mapLaptopModelVOToLaptopModel(LaptopModelVO laptopModelVO);
}
