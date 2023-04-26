package com.parsing.rest;

import com.parsing.rozetka.RozetkaParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rozetka-parser")
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
