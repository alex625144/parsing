package com.parsing.schedulers;

import com.parsing.model.LaptopItem;
import com.parsing.model.LotInfo;
import com.parsing.model.LotItemInfo;
import com.parsing.model.LotResult;
import com.parsing.model.Status;
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
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    @Transactional
    @Scheduled(initialDelay = TEN_MINUTES, fixedDelay = TEN_MINUTES)
    public void mapLotInfo() {
        List<LotResult> lotResults = lotResultService.findAllPDFParserLots();
        List<LotInfo> lotInfos = new ArrayList<>(lotResults.size());

        for (LotResult lotResult : lotResults) {
            List<LotItemInfo> lotItemInfos;

            LotInfo lotInfo = LotInfo.builder()
                    .buyer(lotResult.getBuyer())
                    .seller(lotResult.getSeller())
                    .lotStatus(lotResult.getLotStatus())
                    .dk(lotResult.getDk())
                    .lotTotalPrice(lotResult.getLotTotalPrice())
                    .lotURL(lotResult.getLotURL().isEmpty() ?
                            null : lotResult.getLotURL())
                    .pdfURL(lotResult.getPdfURL())
                    .lotItems(Objects.nonNull(lotResult.getLotPDFResult()) ?
                            parsLotInfo(lotResult.getLotPDFResult().getLaptopItems()) : null)
                    .lotResult(lotResult)
                    .build();

            lotInfos.add(lotInfo);
            lotResult.setStatus(lotResult.getStatus() == Status.PDF_SUCCESSFULL ? Status.MAPPED_TO_INFO_SUCCESSFULL : Status.MAPPED_TO_INFO_FAILED);
        }

        lotResultService.saveAll(lotResults);
        lotInfoService.saveAll(lotInfos);
    }

    @Transactional
    @Scheduled(initialDelay = TEN_MINUTES, fixedDelay = TEN_MINUTES)
    public void parsLotResultAndLotPDFResult() throws IOException {
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

    private List<LotItemInfo> parsLotInfo(List<LaptopItem> laptopItems) {
        if (laptopItems == null || laptopItems.isEmpty()) {
            return null;
        }

        return laptopItems.stream()
                .map(laptopItem -> LotItemInfo.builder()
                        .model(laptopItem.getModel())
                        .amount(laptopItem.getAmount())
                        .price(laptopItem.getPrice())
                        .totalItemPrice(laptopItem.getPrice()
                                .multiply(BigDecimal.valueOf(laptopItem.getAmount())))
                        .build())
                .toList();
    }
}

