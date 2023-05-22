package com.parsing.parsers.pdf.parsing;


import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.parsers.pdf.download.DownloaderPDF;
import com.parsing.repository.LotResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerParsingPDF {

    private static final long TEN_MINUTES = 600000L;
    private final LotResultRepository lotResultRepository;
    private final DownloaderPDF downloaderPDF;

    @Scheduled(initialDelay = TEN_MINUTES, fixedDelay = TEN_MINUTES)
    //todo check two weeks lotresult and try download and parse
    private void SchedulingParsingPDF() {
        List<LotResult> lotResults = lotResultRepository.findAllByStatus(Status.PARSED);
        for (LotResult lotResult : lotResults) {
            if (downloaderPDF.downloadByUrl(URI.create(lotResult.getPdfLink()))) {
                lotResult.setStatus(Status.DOWNLOADED);
            }
        }

    }
}
