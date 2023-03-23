package com.parsing.web;

import com.parsing.pdf.download.PDFDownloader;
import com.parsing.pdf.parsing.ParsingPDFService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProzzoroPDFReaderResource {

    private final ParsingPDFService parsingPDFService;
    private final PDFDownloader pdfDownloader;

    @PostMapping(value = "/api/pdf/extractText", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String extractTextFromPDFFile(@RequestPart(value = "upload") MultipartFile file) {
        return parsingPDFService.parseProzorroFile(file);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/api/pdf/download", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void downloadPDFFile(@RequestParam(value = "uri") String uri) {
        pdfDownloader.downloadDocument(uri);
    }
}
