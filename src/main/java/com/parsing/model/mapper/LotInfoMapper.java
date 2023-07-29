package com.parsing.model.mapper;

import com.parsing.model.LotInfo;
import com.parsing.model.LotResult;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = LotItemInfoMapper.class)
public interface LotInfoMapper {

    @IterableMapping(qualifiedByName = "toLotInfo")
    List<LotInfo> toLotInfoList(List<LotResult> lotResults);

    @Named("toLotInfo")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participants", expression = "java(List.copyOf(lotResult.getParticipants()))")
    @Mapping(target = "lotItems", source = "lotPDFResult.laptopItems", qualifiedByName = "toLotItemInfoList")
    @Mapping(target = "lotResult", source = ".")
    LotInfo toLotInfo(LotResult lotResult);
}
