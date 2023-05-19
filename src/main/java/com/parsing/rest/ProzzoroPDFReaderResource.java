package com.parsing.rest;

import com.parsing.service.PDFDownloaderService;
import com.parsing.parsers.pdf.parsing.ParsingPDFService;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ProzzoroPDFReaderResource {

    private final ParsingPDFService parsingPDFService;
    private final PDFDownloaderService pdfDownloader;


    @PostMapping(value = "/api/pdf/extractText", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String extractTextFromPDFFile(@RequestPart(value = "upload") MultipartFile file) throws IOException, TesseractException {
        return parsingPDFService.parseProzorroFile(file);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/api/pdf/download", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void downloadPDFFile(@RequestParam(value = "uri") String uri) {
        pdfDownloader.downloadPDF(uri);
    }
}
