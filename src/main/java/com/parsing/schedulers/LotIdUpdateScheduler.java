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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableAsync
@EnableScheduling
public class LotIdUpdateScheduler {

    private final LotIdRepository lotIdRepository;

    private final LotIdExtractor lotIdExtractor;

    @Async
    @Scheduled(initialDelayString = "${lotid_update.initial_time}", fixedDelayString = "${for_all_schedulers.update_time}")
    public void scheduled() {
        log.info("Scheduler for download lotId started.");
        List<LotId> listLotId = lotIdRepository.findAll();
        ZonedDateTime max =listLotId.get(0).getDateModified();
        for (LotId lotId : listLotId) {
            if (lotId.getDateModified().compareTo(max) > 0) {
                max = lotId.getDateModified();
            }
        }
        OffsetDateTime offsetTime = max.toOffsetDateTime();
        lotIdExtractor.tryExtractLots(offsetTime.toString());
        log.info("Scheduler for download lotId finished.");
    }
}
