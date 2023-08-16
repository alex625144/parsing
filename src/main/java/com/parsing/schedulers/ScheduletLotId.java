package com.parsing.schedulers;

import com.parsing.service.APIParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private static final long ONE_HOUR = 3_600_000L;

    private static final Long UPDATE_TIME = 36_000_000L;

    private final APIParserService apiParserService;

    @Async
    @Scheduled(initialDelay = ONE_HOUR, fixedDelay = UPDATE_TIME)
    public void scheduled() {
        log.info("Scheduler for download lotId started.");
        apiParserService.parseLotId();
        log.info("Scheduler for download lotId finished.");
    }
}
