package com.parsing.config;

import com.parsing.parsers.parserlot.openai.ChatGPTRequestBody;
import com.parsing.parsers.parserlot.openai.DTO.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Configuration
public class OpenAIConfiguration {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.temperature}")
    private double temperature;

    private final static String CHAT_GPT_PATTERN = "src/main/resources/chatgpt/chatgp-parsing-pattern.txt";

    @Bean
    public RestTemplate openaiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);

            return execution.execute(request, body);
        });

        return restTemplate;
    }

    @Bean
    public ChatGPTRequestBody createChatGptMessenger() {
        try (FileReader fileReader = new FileReader(CHAT_GPT_PATTERN);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            StringBuffer buffer = new StringBuffer();

            bufferedReader.lines()
                    .forEach(buffer::append);
            String patternText = buffer.toString();

            ChatGPTRequestBody messenger = new ChatGPTRequestBody();
            messenger.setModel(model);
            messenger.setTemperature(temperature);
            messenger.newPattern(new Pattern(patternText));

            return messenger;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
