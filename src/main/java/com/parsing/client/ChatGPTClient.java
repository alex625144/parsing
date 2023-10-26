package com.parsing.client;

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

    public ChatResponse execute(String hOcr) {
        chatGPTRequestBody.newRequest(hOcr);
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + "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");

            return execution.execute(request, body);
        });
        return restTemplate.postForObject(apiUrl, chatGPTRequestBody, ChatResponse.class);
    }

}