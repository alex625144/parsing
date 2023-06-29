package com.parsing.service;

import com.parsing.parsers.pdf.download.DownloaderPDF;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DownloaderPDFService {

    private final DownloaderPDF downloaderPDF;

    public Path downloadPDF(String documentUrl, UUID uuid) {
        return downloaderPDF.downloadByUrl(URI.create(documentUrl), uuid);
    }

}
