package com.parsing.schedulers;

import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import com.parsing.service.DownloaderPDFService;
import com.parsing.service.ParserPDFService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@EnableAsync
public class PDFParserScheduler {

    private static final long THREE_HOUR = 10_800_000L;
    private final long UPDATE_TIME = 36_000_000L;

    private final LotResultRepository lotResultRepository;

    private final DownloaderPDFService downloaderPDFService;

    private final ParserPDFService parserPDFService;

    @Async
    @Scheduled(initialDelay = THREE_HOUR, fixedDelay = UPDATE_TIME)
    public void scheduled() {
        List<LotResult> lotResults = lotResultRepository.findAllByStatusAndPdfURLNotNull(Status.CREATED);
        for (LotResult lotResult : lotResults) {
            Path filename = downloaderPDFService.downloadPDF(lotResult.getLotURL(), lotResult.getId());
            if (filename != null) {
                lotResult.setStatus(Status.DOWNLOADED);
            }
            File fileForParse = new File(filename.toString());
            if (parserPDFService.parsePDF(fileForParse)) {
                lotResult.setStatus(Status.PDF_SUCCESSFULL);
                fileForParse.delete();
            } else {
                lotResult.setStatus(Status.PDF_FAILED);
            }
        }
    }
}

