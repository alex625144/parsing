package com.parsing.api;

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

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExtractorLotInfo {

    private final String URL_LOT = "https://public.api.openprocurement.org/api/2.5/tenders/";
    private final String LOT_STATUS = "complete";
    JsonNode jsonNode = null;
    private final LotResultRepository lotResultRepository;
    private final ParticipantRepository participantRepository;

    public void extractLotInformation(String idLot) {
        try {
            log.debug(URL_LOT + idLot);
            URI url = new URI(URL_LOT + idLot);
            System.out.println(URL_LOT+idLot);
            getLotsFromURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean getLotsFromURL(URI uri) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            String jsonData = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(jsonData);
        }
        final JsonNode data = jsonNode.get("data");
        saveLotResult(data);
        return true;
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
            BigDecimal priceB = new BigDecimal(price);
            lotResult.setLotTotalPrice(priceB);
        }
    }

    private void extractSeller(JsonNode data, LotResult lotResult) {
        Participant seller = new Participant();
        JsonNode awards = data.get("awards");
        for (JsonNode award : awards) {
            JsonNode suppliers = award.get("suppliers");
            for (JsonNode supplier : suppliers) {
                seller.setName(supplier.get("identifier").get("legalName").textValue());
                seller.setEdrpou(supplier.get("identifier").get("id").textValue());
            }
        }
        participantRepository.save(seller);
        lotResult.setSeller(seller);
    }

    private static void extractPdfUrl(JsonNode data, LotResult lotResult) {
        JsonNode contracts = data.get("contracts");
        String pdfUrl = null;
        for (JsonNode contract : contracts) {
            JsonNode documents = contract.get("documents");
            for (int i = 0; i < documents.size(); i++) {
                if (i == 0) {
                    pdfUrl = documents.get(i).get("url").textValue();
                }
            }
        }
        lotResult.setPdfURL(pdfUrl);
    }

    private static void extractLotUrl(JsonNode data, LotResult lotResult) {
        String lotUrl = "https://prozorro.gov.ua/tender/" + data.get("tenderID").textValue();
        lotResult.setLotURL(lotUrl);
    }

    private static void extractDateModified(JsonNode data, LotResult lotResult) {
        String dateModified = data.get("dateModified").textValue();
        lotResult.setDateModified(dateModified);
    }


    private static void extractDk(JsonNode data, LotResult lotResult) {
        JsonNode items = data.get("items");
        for (JsonNode item : items) {
            lotResult.setDk(item.get("classification").get("id").textValue());
        }
    }

    private void extractParticipants(JsonNode data, LotResult lotResult) {
        JsonNode jsonNode = data.get("bids");
        List<Participant> participants = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            JsonNode tenderers = node.get("tenderers");
            for (JsonNode jsonNode1 : tenderers) {
                Participant participant = new Participant();
                participant.setName(jsonNode1.get("name").textValue());
                participant.setEdrpou(jsonNode1.get("identifier").get("id").textValue());
                participants.add(participant);
                participantRepository.save(participant);
            }
        }
        lotResult.setParticipants(participants);
    }

    private void extractBuyer(JsonNode data, LotResult lotResult) {
        Participant buyer = new Participant();
        buyer.setName(data.get("procuringEntity").get("name").textValue());
        buyer.setEdrpou(data.get("procuringEntity").get("identifier").get("id").textValue());
        lotResult.setLotStatus(LotStatus.COMPLETE);
        participantRepository.save(buyer);
        lotResult.setBuyer(buyer);
    }
}

