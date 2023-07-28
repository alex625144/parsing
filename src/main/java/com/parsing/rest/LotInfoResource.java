package com.parsing.rest;

import com.parsing.schedulers.Scheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LotInfoResource {

    private final Scheduler scheduler;

    @GetMapping("/lot-info")
    void testLotInfoSchedule() {
        scheduler.mapLotInfo();
    }
}
