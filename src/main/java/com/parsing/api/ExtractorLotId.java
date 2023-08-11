package com.parsing.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.model.LotId;
import com.parsing.repository.LotIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExtractorLotId {

    private final ObjectMapper objectMapper;

    private final LotIdRepository lotIdRepository;

    private final RestTemplate restTemplate;

    private String offset = null;

    @Value("${time.offset}")
    private void setTimeOffset(String timeOffset) {
        if (timeOffset != null && !timeOffset.isEmpty()) {
            offset = timeOffset;
        }
    }

    public void tryExtractLots() {
        String START_DATE_URL = "https://public.api.openprocurement.org/api/2.5/tenders?offset=" + offset;
        JsonNode jsonNode;
        ResponseEntity<String> response;
        URI uri;
        try {
            uri = new URI(START_DATE_URL);
            log.info("URI {} saved to db", uri);
            response = restTemplate.getForEntity(uri, String.class);
            jsonNode = objectMapper.readTree(response.getBody());
        } catch (URISyntaxException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        saveLot(jsonNode.get("data"));
        JsonNode nextPage = jsonNode.get("next_page");
        Optional<URI> nextPageUri;
        try {
            nextPageUri = Optional.of(new URI(nextPage.get("uri").textValue()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        while (nextPageUri.isPresent()) {
            log.info("URI {} saved to db", nextPageUri.get());
            response = restTemplate.getForEntity(nextPageUri.get(), String.class);

            try {
                jsonNode = objectMapper.readTree(response.getBody());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            saveLot(jsonNode.get("data"));
            nextPage = jsonNode.get("next_page");
            try {
                nextPageUri = Optional.of(new URI(nextPage.get("uri").textValue()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

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