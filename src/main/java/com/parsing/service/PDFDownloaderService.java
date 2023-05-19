package com.parsing.service;

import com.parsing.parsers.pdf.download.DownloaderPDF;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class PDFDownloaderService {

    private final DownloaderPDF downloaderPDF;

    public void downloadPDF(String documentUrl) {
        downloaderPDF.downloadByUrl(URI.create(documentUrl));
    }

}
