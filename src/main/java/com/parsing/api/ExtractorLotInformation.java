package com.parsing.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.model.LotResult;
import com.parsing.model.LotStatus;
import com.parsing.model.Participant;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import com.parsing.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExtractorLotInformation {

    private final String URL_LOT = "https://public.api.openprocurement.org/api/2.5/tenders/";
    private final String LOT_STATUS = "complete";

    private final LotResultRepository lotResultRepository;

    private final ParticipantRepository participantRepository;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public void extractLotInformation(String lotId) {
        ResponseEntity<String> response;
        URI uri;
        try {
            uri = new URI(URL_LOT+lotId);
            log.info(String.valueOf(uri));
            response = restTemplate.getForEntity(uri, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode data = jsonNode.get("data");
            saveLotResult(data);
        } catch (URISyntaxException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void saveLotResult(JsonNode data) {
        LotResult lotResult = new LotResult();
        String status = data.get("status").textValue();
        System.out.println(status);
        if (status.equals(LOT_STATUS)) {
            extractLotTotalPrice(data, lotResult);
            extractBuyer(data, lotResult);
            extractParticipants(data, lotResult);
            extractDk(data, lotResult);
            extractLotUrl(data, lotResult);
            extractPdfUrl(data, lotResult);
            extractSeller(data, lotResult);
            extractDateModified(data, lotResult);
            lotResult.setStatus(Status.CREATED);
            lotResultRepository.save(lotResult);
        }
    }

    private void extractLotTotalPrice(JsonNode data, LotResult lotResult) {
        JsonNode contracts = data.get("contracts");
        for (JsonNode contract : contracts) {
            String price = contract.get("value").get("amount").toString();
            BigDecimal lotTotalPrice = new BigDecimal(price);
            lotResult.setLotTotalPrice(lotTotalPrice);
        }
    }

    private void extractBuyer(JsonNode data, LotResult lotResult) {
        Participant buyer = new Participant();
        buyer.setName(data.get("procuringEntity").get("name").toString());
        buyer.setEdrpou(data.get("procuringEntity").get("identifier").get("id").toString());
        lotResult.setLotStatus(LotStatus.COMPLETED_PURCHASE);
        participantRepository.save(buyer);
        lotResult.setBuyer(buyer);
    }

    private void extractParticipants(JsonNode data, LotResult lotResult) {
        JsonNode jsonNode = data.get("bids");
        List<Participant> participants = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            JsonNode tenderers = node.get("tenderers");
            for (JsonNode tenderer : tenderers) {
                Participant participant = new Participant();
                participant.setName(tenderer.get("name").toString());
                participant.setEdrpou(tenderer.get("identifier").get("id").toString());
                participants.add(participant);
                participantRepository.save(participant);
            }
        }
        lotResult.setParticipants(participants);
    }

    private static void extractDk(JsonNode data, LotResult lotResult) {
        JsonNode items = data.get("items");
        for (JsonNode item : items) {
            lotResult.setDk(item.get("classification").get("id").toString());
        }
    }

    private static void extractLotUrl(JsonNode data, LotResult lotResult) {
        String lotUrl = "https://prozorro.gov.ua/tender/" + data.get("tenderID").toString();
        lotResult.setLotURL(lotUrl);
    }

    private static void extractPdfUrl(JsonNode data, LotResult lotResult) {
        JsonNode contracts = data.get("contracts");
        String pdfUrl = null;
        for (JsonNode contract : contracts) {
            JsonNode documents = contract.get("documents");
            pdfUrl = documents.get(0).get("url").toString();
        }
        lotResult.setPdfURL(pdfUrl);
    }

    private void extractSeller(JsonNode data, LotResult lotResult) {
        Participant seller = new Participant();
        JsonNode awards = data.get("awards");
        for (JsonNode award : awards) {
            JsonNode suppliers = award.get("suppliers");
            for (JsonNode supplier : suppliers) {
                seller.setName(supplier.get("identifier").get("legalName").toString());
                seller.setEdrpou(supplier.get("identifier").get("id").toString());
            }
        }
        participantRepository.save(seller);
        lotResult.setSeller(seller);
    }

    private static void extractDateModified(JsonNode data, LotResult lotResult) {
        String dateModified = data.get("dateModified").textValue();
        lotResult.setDateModified(dateModified);
    }
}

