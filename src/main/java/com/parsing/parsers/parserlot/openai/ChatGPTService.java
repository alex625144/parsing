package com.parsing.parsers.parserlot.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.parsers.parserlot.openai.DTO.LaptopModelVO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final ObjectMapper objectMapper;

    private final ChatGPTClient chatGPTClient;

    @SneakyThrows
    public LaptopModelVO parseModel(String model) {
        String result = chatGPTClient.parseModel(model);

        return objectMapper.readValue(result, LaptopModelVO.class);
    }
}
