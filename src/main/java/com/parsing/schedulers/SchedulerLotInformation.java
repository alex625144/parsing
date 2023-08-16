package com.parsing.schedulers;

import com.parsing.service.APIParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
@EnableAsync
public class SchedulerLotInformation {

    private static final long THREE_MINUTES = 180000L;

    private final APIParserService apiParserService;

    @Async
    @Scheduled(initialDelay = THREE_MINUTES, fixedDelay = THREE_MINUTES)
    public void scheduled() {
        log.info("Scheduler for download lotInformation started.");
        apiParserService.parseLotInformation();
        log.info("Scheduler for download lotInformation finished.");
    }
}
