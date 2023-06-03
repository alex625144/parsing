package com.parsing.rest;

import com.parsing.parsers.parserlot.openai.ChatGPTService;
import com.parsing.parsers.parserlot.openai.DTO.LaptopModelVO;
import com.parsing.parsers.parserlot.openai.DTO.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatgpt-parsing")
public class ChatGPTResource {

    private final ChatGPTService chatGPTService;

    @PostMapping
    public LaptopModelVO createNewChatPattern(@RequestBody Pattern pattern) {
        chatGPTService.newPattern(pattern);

        return chatGPTService.execute();
    }

    @PostMapping("/addRule")
    public LaptopModelVO addRule(@RequestBody Pattern pattern) {
        chatGPTService.addRuleToPattern(pattern);

        return chatGPTService.execute();
    }

    @GetMapping("/{prompt}")
    public LaptopModelVO chat(@PathVariable String prompt) {
        chatGPTService.newRequest(prompt);

        return chatGPTService.execute();
    }
}
