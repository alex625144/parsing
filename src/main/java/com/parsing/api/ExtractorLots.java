package com.parsing.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.api.model.LotID;
import com.parsing.api.repository.LotIDRepository;
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
public class ExtractorLots {

    private final String OFFSET = "1672534861"; // this date 2023-01-01
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = null;
    private final LotIDRepository lotIDRepository;

    public boolean extractLots() {

        String START_DATE = "https://public.api.openprocurement.org/api/2.5/tenders?offset=" + OFFSET;
        try {
            URL url = new URL(START_DATE);
            return getLotsFromURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
    void saveLot(JsonNode data) {
        for (JsonNode lot : data) {
            String dateModified = lot.get("dateModified").toString();
            String id = lot.get("id").toString();
            LotID lotID = new LotID();
            lotID.setId(id);
            lotID.setDateModified(dateModified);
            lotIDRepository.save(lotID);
        }
    }
}
