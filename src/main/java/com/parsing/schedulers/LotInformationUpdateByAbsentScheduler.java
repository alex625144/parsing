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
public class LotInformationUpdateByAbsentScheduler {

    private static final long FOUR_HOURS = 24_000_000;

    private final LotIdRepository lotIdRepository;

    private final LotResultRepository lotResultRepository;

    private final LotInformationExtractor lotInformationExtractor;

    @Async
    @Scheduled(initialDelay = FOUR_HOURS, fixedDelayString = "${update.time}")
    public void scheduled() {
        log.info("Scheduler for UPDATE absent lotInformation started.");
        List<LotId> listLotId = lotIdRepository.findAll();
        List<LotResult> listLotResult = lotResultRepository.findAll();
        List<LotId> listLotIdForUpdate = listLotId.stream().filter(lotId ->
                listLotResult.stream().noneMatch(lotResult -> lotResult.getId().equals(lotId.getId()))).toList();
        log.info("Lots for update = " + listLotIdForUpdate.size());
        lotInformationExtractor.tryExtractLotsInformation(listLotIdForUpdate);
        log.info("Scheduler for UPDATE absent lotInformation finished.");
    }
}