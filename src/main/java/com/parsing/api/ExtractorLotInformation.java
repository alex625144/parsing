package com.parsing.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.api.model.LotId;
import com.parsing.api.repository.LotIdRepository;
import com.parsing.model.LotResult;
import com.parsing.repository.LotResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExtractorLotInformation {


    ObjectMapper objectMapper = new ObjectMapper();
    private final String URL_LOT = "https://public.api.openprocurement.org/api/2.5/tenders/";
    JsonNode jsonNode = null;
    private final LotResultRepository lotResultRepository;

    public boolean extractLotInformation(String idLot) {
        try {
            URL url = new URL(URL_LOT + idLot);
            return getLotsFromURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean getLotsFromURL(URL url) throws IOException {
        try {
            jsonNode = objectMapper.readTree(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final JsonNode data = jsonNode.get("data");
        saveLotResult(data);


        return true;
    }

    @Transactional
    public void saveLotResult(JsonNode data) {
        for (JsonNode lot : data) {
            String dateModified = lot.get("dateModified").toString();
            String id = lot.get("id").toString();

            LotResult lotResult = new LotResult();
            lotResult.setId(UUID.fromString(lot.get("id").toString()));
            System.out.println(lot.get("data").get("0").get("status"));



            lotResultRepository.save(lotResult);
        }
    }
}
