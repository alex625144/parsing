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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@EnableAsync
public class Scheduler {

    private static final long TEN_MINUTES = 600000L;
    private static final int MINIMAL_SIZE_PDF_FILE = 50000;

    private final LotResultRepository lotResultRepository;
    private final DownloaderPDFService downloaderPDFService;
    private final ParserPDFService parserPDFService;

    @Async
    @Scheduled(initialDelay = TEN_MINUTES, fixedDelay = TEN_MINUTES)
    public void scheduled() throws IOException {
        List<LotResult> lotResults = lotResultRepository.findAllByStatusAndLotPDFResultIsNull(Status.CREATED);
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
}

