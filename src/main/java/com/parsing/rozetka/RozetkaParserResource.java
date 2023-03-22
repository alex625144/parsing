package com.parsing.rozetka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rozetka-parser")
public class RozetkaParserResource {

    private final RozetkaParserSevice rozetkaSevice;

    @GetMapping("/{laptop}")
    public String findPriceByURL(@PathVariable String laptop) {
        return rozetkaSevice.findPriceByURL(laptop);
    }
}
