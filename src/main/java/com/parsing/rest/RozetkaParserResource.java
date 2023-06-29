package com.parsing.rest;

import com.parsing.service.RozetkaParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rozetka-parser")
public class RozetkaParserResource {

    private final RozetkaParserService rozetkaSevice;

    @GetMapping("/{model}")
    public String findPriceByModel(@PathVariable String model) {
        return rozetkaSevice.findPriceByModel(model);
    }

    @PostMapping
    public void startParsing() {
        rozetkaSevice.scheduledParsing();
    }
}
