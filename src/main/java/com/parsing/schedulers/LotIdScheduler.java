package com.parsing.schedulers;

import com.parsing.api.LotIdExtractor;
import com.parsing.model.LotId;
import com.parsing.repository.LotIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableAsync
@EnableScheduling
public class LotIdScheduler {

    private static final long ONE_HOUR = 1000;

    private static final long UPDATE_TIME = 36_000_000L;

    private final LotIdRepository lotIdRepository;

    private final LotIdExtractor lotIdExtractor;

    @Async
    @Scheduled(initialDelay = ONE_HOUR, fixedDelay = UPDATE_TIME)
    public void scheduled() {
        log.info("Scheduler for download lotId started.");
        Optional<LotId> lastLotId = lotIdRepository.findTopByOrderByIdDesc();
        ZonedDateTime lastDateModified = null;
        if(lastLotId.isPresent()) {
            lastDateModified = lastLotId.get().getDateModified();
        }
        OffsetDateTime offsetTime = lastDateModified.toOffsetDateTime();
        lotIdExtractor.tryExtractLots(offsetTime.toString());
        log.info("Scheduler for download lotId finished.");
    }
}
