package com.parsing.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.api.model.LotId;
import com.parsing.api.repository.LotIdRepository;
import com.parsing.model.LotResult;
import com.parsing.model.LotStatus;
import com.parsing.model.Participant;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import com.parsing.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.metadata.HsqlTableMetaDataProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExtractorLotInformation {

    ObjectMapper objectMapper = new ObjectMapper();
    private final String URL_LOT = "https://public.api.openprocurement.org/api/2.5/tenders/";
    JsonNode jsonNode = null;
    private final LotResultRepository lotResultRepository;
    private final ParticipantRepository participantRepository;

    public boolean extractLotInformation(String idLot) {
        try {
            log.debug(URL_LOT + idLot);
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
        // get lotStatus
        String FIND = "\"complete\"";
        JsonNode statuses = data.get("status");
        String status = statuses.toString();
        System.out.println(status);
        if (status.equals(FIND)) {
            System.out.println("here1");
            Participant buyer = new Participant();
            buyer.setName(data.get("procuringEntity").get("name").toString());
            buyer.setEdrpou(data.get("procuringEntity").get("identifier").get("id").toString());
            LotResult lotResult = new LotResult();
            lotResult.setLotStatus(LotStatus.COMPLETE);
            participantRepository.save(buyer);
            lotResult.setBuyer(buyer);
            // get participants
            JsonNode jsonNode = data.get("bids");
            List<Participant> participants = new ArrayList<>();
            for (JsonNode node : jsonNode) {
                JsonNode tenderers = node.get("tenderers");
                for (JsonNode jsonNode1 : tenderers) {
                    Participant participant = new Participant();
                    participant.setName(jsonNode1.get("name").toString());
                    participant.setEdrpou(jsonNode1.get("identifier").get("id").toString());
                    participants.add(participant);
                    participantRepository.save(participant);
                }
            }
            lotResult.setParticipants(participants);
            // get dk
            JsonNode items = data.get("items");
            for (JsonNode item : items) {
                lotResult.setDk(item.get("classification").get("id").toString());
            }
            //set lotTotalPrice
            JsonNode contracts = data.get("contracts");
            for (JsonNode contract : contracts) {
                String price = contract.get("value").get("amount").toString();
                BigDecimal priceB = new BigDecimal(price);
                lotResult.setLotTotalPrice(priceB);
            }
            //set loturl
            String lotUrl = "https://prozorro.gov.ua/tender/" + data.get("tenderID").toString();
            lotResult.setLotURL(lotUrl);
            //set pdfurl
            String pdfUrl = null;
            for (JsonNode contract : contracts) {
                JsonNode documents = contract.get("documents");
                for (int i = 0; i < documents.size(); i++) {
                    if (i == 0) {
                        pdfUrl = documents.get(i).get("url").toString();
                    }
                }
            }
            lotResult.setPdfURL(pdfUrl);
            //set seller
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
            lotResult.setStatus(Status.CREATED);
            lotResultRepository.save(lotResult);
        }
    }
}

