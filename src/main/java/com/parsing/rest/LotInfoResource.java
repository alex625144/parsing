package com.parsing.rest;

import com.parsing.schedulers.LotInfoUpdateScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LotInfoResource {

    private final LotInfoUpdateScheduler lotInfoUpdateScheduler;

    @GetMapping("/lot-infos")
    void testLotInfoSchedule() {
        lotInfoUpdateScheduler.mapLotInfo();
    }
}
