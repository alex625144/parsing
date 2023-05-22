package com.parsing.schedulers;


import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import com.parsing.service.DownloaderPDFService;
import com.parsing.service.ParserPDFService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private static final long TEN_MINUTES = 600000L;

    private final LotResultRepository lotResultRepository;
    private final DownloaderPDFService downloaderPDFService;
    private final ParserPDFService parserPDFService;

    //    @Scheduled(initialDelay = TEN_MINUTES, fixedDelay = TEN_MINUTES)
    //todo check two weeks lotresult and try download and parse
    public void scheduled() throws IOException {
        List<LotResult> lotResults = lotResultRepository.findAllByStatus(Status.PARSED);
        for (LotResult lotResult : lotResults) {
            Path filename = downloaderPDFService.downloadPDF(lotResult.getPdfLink(), lotResult.getId());
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

