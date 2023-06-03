package com.parsing.parsers.parserlot.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsing.parsers.parserlot.openai.DTO.ChatResponse;
import com.parsing.parsers.parserlot.openai.DTO.LaptopModelVO;
import com.parsing.parsers.parserlot.openai.DTO.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    @Value("${openai.api.url}")
    private String apiUrl;

    private final ChatGPTMessenger chatGPTMessenger;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public LaptopModelVO execute() {
        ChatResponse response = restTemplate.postForObject(apiUrl, chatGPTMessenger, ChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return null;
        }

        return objectMapper.readValue(response.getChoices().get(0).getMessage().getContent(), LaptopModelVO.class);
    }

    public void newRequest(String prompt) {
        chatGPTMessenger.newRequest(prompt);
    }

    public void addRuleToPattern(Pattern pattern) {
        chatGPTMessenger.addRuleToPattern(pattern);
    }

    public void newPattern(Pattern pattern) {
        chatGPTMessenger.newPattern(pattern);
    }
}

