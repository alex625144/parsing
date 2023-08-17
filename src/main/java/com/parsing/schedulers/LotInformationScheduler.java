package com.parsing.schedulers;

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
public class LotInformationScheduler {

    private static final long TWO_HOUR = 7_200_000L;
    private static final long UPDATE_TIME = 36_000_000L;

    @Async
    @Scheduled(initialDelay = TWO_HOUR, fixedDelay = UPDATE_TIME)
    public void scheduled() {
        log.info("Scheduler for download lotInformation started.");

        log.info("Scheduler for download lotInformation finished.");
    }
}
