package com.parsing.schedulers;

import com.parsing.service.APIParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableAsync
@EnableScheduling
public class ScheduletLotId {

    private static final long ONE_MINUTE = 60000L;

    private final APIParserService apiParserService;

    @Async
    @Scheduled(initialDelay = ONE_MINUTE, fixedDelay = ONE_MINUTE)
    public void scheduled() {
        log.info("Scheduler for download lotId started.");
        apiParserService.parseLotId();
        log.info("Scheduler for download lotId finished.");
    }
}
