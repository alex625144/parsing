package com.parsing.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.api.model.LotId;
import com.parsing.api.repository.LotIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExtractorLotId {

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = null;
    private final LotIdRepository lotIDRepository;

    public boolean extractLots() {

        // this date 2023-01-01
        String OFFSET = "1672534861";
        String START_DATE = "https://public.api.openprocurement.org/api/2.5/tenders?offset=" + OFFSET;
        try {
            URL url = new URL(START_DATE);
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
        saveLot(data);
        final JsonNode nextPage = jsonNode.get("next_page");
        final JsonNode uri = nextPage.get("uri");
        System.out.println(uri);
        if (!nextPage.isEmpty()) {
            try {
                URL url1 = new URL(uri.asText());
                System.out.println(url1);
                return getLotsFromURL(url1);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Transactional
    public void saveLot(JsonNode data) {
        for (JsonNode lot : data) {
            String dateModified = lot.get("dateModified").toString();
            String id = lot.get("id").toString();
            LotId lotID = new LotId();
            lotID.setId(id);
            lotID.setDateModified(dateModified);
            lotIDRepository.save(lotID);
        }
    }
}