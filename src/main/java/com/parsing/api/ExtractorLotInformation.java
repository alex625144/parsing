package com.parsing.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.api.model.LotId;
import com.parsing.api.repository.LotIdRepository;
import com.parsing.model.LotResult;
import com.parsing.model.Participant;
import com.parsing.repository.LotResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public boolean extractLotInformation(String idLot) {
        try {
            System.out.println(URL_LOT+idLot);
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
        // get buyer
        Participant buyer = new Participant();
        buyer.setName(data.get("procuringEntity").get("name").toString());
        buyer.setEdrpou(data.get("procuringEntity").get("identifier").get("id").toString());
        LotResult lotResult = new LotResult();
        lotResult.setBuyer(buyer);
        // get participants
        JsonNode jsonNode = data.get("bids");
        List<Participant> participants = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            Participant participant = new Participant();
            participant.setName(node.get("tenderers").get("name").toString());
            participant.setEdrpou(node.get("tenderers").get("identifier").get("id").toString());
            participants.add(participant);
        }
        lotResult.setParticipants(participants);
        // get dk
        lotResult.setDk(data.get("items").get("classfication").get("id").toString());
        //set lotTotalPrice
        String price  = data.get("contracts").get("value").get("amount").toString();
        BigDecimal priceB = new BigDecimal(price);
        lotResult.setLotTotalPrice(priceB);
        //set loturl
        String lotUrl = "https://prozorro.gov.ua/tender/" + data.get("data").get("criteria").get("tenderID").toString();
        lotResult.setLotURL(lotUrl);
        //set pdfurl
        String pdfUrl = data.get("data").get("contracts").get("documents").get("url").toString();
        lotResult.setPdfURL(pdfUrl);
        //set seller
        Participant seller = new Participant();
        seller.setName(data.get("awards").get("suppliers").get("identifiers").get("legalName").toString());
        seller.setEdrpou(data.get("awards").get("suppliers").get("identifiers").get("id").toString());
        //set lotStatus
        lotResult.setLotStatus(data.get("complete").toString());

    }
}
