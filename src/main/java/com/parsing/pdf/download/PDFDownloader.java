package com.parsing.pdf.download;

import com.parsing.client.ProzorroClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class PDFDownloader {

    private final ProzorroClient prozorroClient;

    public void downloadPDF(String documentUrl) {
        String pathToTheSavedFile = prozorroClient.downloadByUrl(URI.create(documentUrl));
    }
}
