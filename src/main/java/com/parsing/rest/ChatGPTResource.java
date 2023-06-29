package com.parsing.rest;

import com.parsing.parsers.parserlot.openai.ChatGPTService;
import com.parsing.parsers.parserlot.openai.DTO.LaptopModelVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatgpt-parsing")
public class ChatGPTResource {

    private final ChatGPTService chatGPTService;

    @GetMapping
    public LaptopModelVO chat(@RequestParam String model) {
        return chatGPTService.parseModel(model);
    }
}
