package com.parsing.model.mapper;

import com.parsing.model.LaptopItem;
import com.parsing.model.LotInfo;
import com.parsing.model.LotItemInfo;
import com.parsing.model.LotResult;
import com.parsing.model.Participant;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public abstract class LotInfoMapper {

    private final LotItemInfoMapper lotItemInfoMapper = Mappers.getMapper(LotItemInfoMapper.class);

    @IterableMapping(qualifiedByName = "toLotInfo")
    public abstract List<LotInfo> toLotInfoList(List<LotResult> lotInfo);

    @Named("toLotInfo")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participants", qualifiedByName = "mapParticipants")
    @Mapping(target = "lotItems", source = "lotPDFResult.laptopItems", qualifiedByName = "toLotItemInfo")
    @Mapping(target = "lotResult", source = ".")
    public abstract LotInfo toLotInfo(LotResult lotInfo);

    @Named("mapParticipants")
    public List<Participant> mapParticipants(List<Participant> participants) {
        return List.copyOf(participants);
    }

    @Named("toLotItemInfo")
    public List<LotItemInfo> toLotItemInfo(List<LaptopItem> laptopItems) {
        if (Objects.isNull(laptopItems) || laptopItems.isEmpty()) {
            return Collections.emptyList();
        }

        return lotItemInfoMapper.toLotItemInfoList(laptopItems);
    }
}
