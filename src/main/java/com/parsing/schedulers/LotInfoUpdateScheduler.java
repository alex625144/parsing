package com.parsing.schedulers;

import com.parsing.model.LotInfo;
import com.parsing.model.LotItemInfo;
import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.model.mapper.LotInfoMapper;
import com.parsing.service.LotInfoService;
import com.parsing.service.LotResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
@EnableScheduling
public class LotInfoUpdateScheduler {

    private final LotResultService lotResultService;

    private final LotInfoService lotInfoService;

    private final LotInfoMapper lotInfoMapper;

    @Async
    @Scheduled(initialDelayString = "${lotinfo.initialtime}", fixedDelayString = "${update.time}")
    public void mapLotInfo() {
        log.info("Scheduler for UPDATE lotInfo started.");
        List<LotResult> lotResults = lotResultService.findAllPDFParserLots();
        List<LotInfo> lotInfos = lotInfoMapper.toLotInfoList(lotResults);
        lotResultService.saveAll(refreshLotResults(lotResults));
        lotInfoService.saveAll(prepareLotInfoToSaving(lotInfos));
        log.info("Scheduler for UPDATE lotInfo finished.");
    }

    private List<LotInfo> prepareLotInfoToSaving(List<LotInfo> lotInfos) {
        for (LotInfo lotInfo : lotInfos) {
            List<LotItemInfo> lotItemInfos = lotInfo.getLotItems();
            if (Objects.nonNull(lotItemInfos)) {
                lotInfo.getLotItems().forEach(lotItemInfo -> lotItemInfo.setLotInfo(lotInfo));
            }
        }
        return lotInfos;
    }

    private List<LotResult> refreshLotResults(List<LotResult> lotResults) {
        lotResults.forEach(lotResult ->
                lotResult.setStatus(lotResult.getStatus() == Status.PDF_SUCCESSFULL ?
                        Status.MAPPED_TO_INFO_SUCCESSFULL : Status.MAPPED_TO_INFO_FAILED));
        return lotResults;
    }
}