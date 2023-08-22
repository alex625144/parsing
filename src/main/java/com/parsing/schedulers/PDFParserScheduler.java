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
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@EnableAsync
public class PDFParserScheduler {

    private static final long TEN_HOURS = 60_000_000;

    private final LotResultRepository lotResultRepository;

    private final DownloaderPDFService downloaderPDFService;

    private final ParserPDFService parserPDFService;

    @Async
    @Scheduled(initialDelayString = "${pdfparser.initialtime}", fixedDelayString = "${update.time}")
    public void scheduled() {
        log.info("Scheduler for PDF parsing started.");
        List<LotResult> lotResults = lotResultRepository.findAllByStatusAndPdfURLNotNull(Status.CREATED);
        for (LotResult lotResult : lotResults) {
            log.debug(lotResult.getLotURL());
            Path filename = downloaderPDFService.downloadPDF(lotResult.getPdfURL(), lotResult.getId());
            if (filename != null) {
                lotResult.setStatus(Status.DOWNLOADED);
            }
            File fileForParse = new File(filename.toString());
            log.debug("File for parse = " + fileForParse);
            if (parserPDFService.parsePDF(fileForParse)) {
                lotResult.setStatus(Status.PDF_SUCCESSFULL);
                fileForParse.delete();
            } else {
                lotResult.setStatus(Status.PDF_FAILED);
                log.debug("PDF parsing was failed for URI {}", lotResult.getPdfURL() );
                fileForParse.delete();
            }
            log.info("Scheduler for PDF parsing finished.");
        }
    }
}

