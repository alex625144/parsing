package com.parsing.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.parsing.model.LotId;
import com.parsing.repository.LotIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaverLotId {

    private final LotIdRepository lotIdRepository;

    @Transactional
    public void saveLot(JsonNode lotIds) {
        for (JsonNode lotId : lotIds) {
            LotId lotID = new LotId();
            lotID.setId(lotId.get("id").textValue());
            ZonedDateTime dateModified = ZonedDateTime.parse(lotId.get("dateModified").textValue());
            lotID.setDateModified(dateModified);
            lotIdRepository.save(lotID);
        }
    }
}
