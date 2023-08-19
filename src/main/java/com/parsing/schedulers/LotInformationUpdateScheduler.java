package com.parsing.schedulers;

import com.parsing.api.LotInformationExtractor;
import com.parsing.model.LotId;
import com.parsing.model.LotResult;
import com.parsing.repository.LotIdRepository;
import com.parsing.repository.LotResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
@EnableAsync
public class LotInformationUpdateScheduler {

    private static final long TWO_HOUR = 6_000_000L;
    private static final long UPDATE_TIME = 36_000_000L;

    private final LotIdRepository lotIdRepository;

    private final LotResultRepository lotResultRepository;

    private final LotInformationExtractor lotInformationExtractor;

    @Async
    @Scheduled(initialDelay = TWO_HOUR, fixedDelay = UPDATE_TIME)
    public void scheduled() {
        log.info("Scheduler for UPDATE missing lotInformation started.");
        List<LotId> listLotId = lotIdRepository.findAll();
        List<LotResult> listLotResult = lotResultRepository.findAll();



        List<LotId> listLotIdForUpdate = listLotId.stream().filter(lotId ->
                listLotResult.stream().noneMatch(lotResult -> lotResult.getId().equals(lotId.getId()))).toList();
        log.info("Lots for update = " + listLotIdForUpdate.size());
        lotInformationExtractor.tryExtractLotsInformation(listLotIdForUpdate);
        log.info("Scheduler for UPDATE missing lotInformation finished.");
    }
}