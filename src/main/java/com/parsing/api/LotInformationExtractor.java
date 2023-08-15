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
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LotInformationExtractor {

    private final LotIdRepository lotIdRepository;

    private final LotInformationSaver lotInformationSaver;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    @Value("${lot.url}")
    private String LOT_URL;

    public void extractLotInformation(String lotId) {
        ResponseEntity<String> response;
        URI uri;
        try {
            uri = new URI(LOT_URL + lotId);
            log.info("URI {} started parsing.", uri);
            response = restTemplate.getForEntity(uri, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode data = jsonNode.get("data");
            lotInformationSaver.saveLotResult(data, lotId);
            log.info("URI {} finished parsing.", uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI syntax is wrong!", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json processing is bad!", e);
        }
    }

    public void extractAllLotsInformation() {
        List<LotId> lotIds = lotIdRepository.findAll();
        for (LotId lotId : lotIds) {
            ResponseEntity<String> response;
            URI uri;
            try {
                uri = new URI(LOT_URL + lotId.getId());
                log.info("URI {} started parsing.", uri);
                response = restTemplate.getForEntity(uri, String.class);
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode data = jsonNode.get("data");
                lotInformationSaver.saveLotResult(data, lotId.getId());
                log.info("URI {} finished parsing.", uri);
            } catch (URISyntaxException e) {
                throw new RuntimeException("URI syntax is wrong!", e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Json processing is bad!", e);
            }
        }
    }
}

