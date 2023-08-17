package com.parsing.schedulers;

import com.parsing.model.LotInfo;
import com.parsing.model.LotItemInfo;
import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.model.mapper.LotInfoMapper;
import com.parsing.service.DownloaderPDFService;
import com.parsing.service.LotInfoService;
import com.parsing.service.LotResultService;
import com.parsing.service.ParserPDFService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private static final long TEN_MINUTES = 600000L;
    private static final int MINIMAL_SIZE_PDF_FILE = 50000;

    private final LotResultService lotResultService;
    private final DownloaderPDFService downloaderPDFService;
    private final ParserPDFService parserPDFService;
    private final LotInfoService lotInfoService;
    private final LotInfoMapper lotInfoMapper;

    @Transactional
    @Scheduled(initialDelay = TEN_MINUTES, fixedDelay = TEN_MINUTES)
    public void parseLotResultAndLotPDFResult() throws IOException {
        List<LotResult> lotResults = lotResultService.findAllByStatus(Status.PARSED);
        for (LotResult lotResult : lotResults) {
            Path filename = downloaderPDFService.downloadPDF(lotResult.getLotURL(), lotResult.getId());
            if (filename != null) {
                lotResult.setStatus(Status.DOWNLOADED);
            }

            File fileForParse = new File(filename.toString());
            Path path = Paths.get(String.valueOf(fileForParse));
            long bytes = Files.size(path);
            log.debug("Size of file " + bytes);
            if (!(bytes < MINIMAL_SIZE_PDF_FILE) && parserPDFService.parsePDF(fileForParse)) {
                lotResult.setStatus(Status.PDF_SUCCESSFULL);
                fileForParse.delete();
            } else {
                lotResult.setStatus(Status.PDF_FAILED);
            }
        }
    }

    @Transactional
    @Scheduled(initialDelay = TEN_MINUTES, fixedDelay = TEN_MINUTES)
    public void mapLotInfo() {
        List<LotResult> lotResults = lotResultService.findAllPDFParserLots();
        List<LotInfo> lotInfos = lotInfoMapper.toLotInfoList(lotResults);

        lotResultService.saveAll(refreshLotResults(lotResults));
        lotInfoService.saveAll(prepareLotInfoToSaving(lotInfos));
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