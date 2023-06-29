package com.parsing.parsers.parserlot.openai;

import com.parsing.parsers.parserlot.openai.DTO.ChatResponse;
import com.parsing.parsers.parserlot.openai.DTO.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ChatGPTClient {

    @Value("${openai.api.url}")
    private String apiUrl;

    private final ChatGPTRequestBody chatGPTRequestBody;
    private final RestTemplate restTemplate;

    public ChatResponse execute() {
        return restTemplate.postForObject(apiUrl, chatGPTRequestBody, ChatResponse.class);
    }

    public String parseModel(String model) {
        newRequest(model);

        return execute().getChoices().get(0).getMessage().getContent();
    }

    public void newRequest(String prompt) {
        chatGPTRequestBody.newRequest(prompt);
    }

    public void addRuleToPattern(Pattern pattern) {
        chatGPTRequestBody.addRuleToPattern(pattern);
    }

    public void newPattern(Pattern pattern) {
        chatGPTRequestBody.newPattern(pattern);
    }
}

