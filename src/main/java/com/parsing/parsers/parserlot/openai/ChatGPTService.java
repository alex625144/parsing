package com.parsing.parsers.parserlot.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.parsers.parserlot.openai.DTO.LaptopModelVO;
import com.parsing.parsers.parserlot.openai.exception.NotExpectedChatGPTResponseBodyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final ObjectMapper objectMapper;

    private final ChatGPTClient chatGPTClient;

    public LaptopModelVO parseModel(String model) {
        String result = chatGPTClient.parseModel(model);

        try {
            return objectMapper.readValue(result, LaptopModelVO.class);
        } catch (JsonProcessingException e) {
            throw new NotExpectedChatGPTResponseBodyException(e);
        }
    }
}
