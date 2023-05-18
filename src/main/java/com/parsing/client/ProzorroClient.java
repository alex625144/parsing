package com.parsing.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProzorroClient {

    private final RestTemplate restTemplate;
    private static final String DIR_TO_SAVE_PDF = "/pdf/";

    public void downloadByUrl(URI uri) {
        String savePath = getPath();
        RequestCallback requestCallback = request -> request
                .getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

        ResponseExtractor<Void> responseExtractor = response -> {
            Path path = Paths.get(savePath);
            Files.copy(response.getBody(), path);
            return null;
        };
        restTemplate.execute(uri, HttpMethod.GET, requestCallback, responseExtractor);
        log.debug("file with URI: " + uri + " was saved.");
    }

    private String getPath() {
        Path currentPathPosition = Paths.get("").toAbsolutePath();
        File pdfDir = new File(currentPathPosition + DIR_TO_SAVE_PDF);
        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }
        return currentPathPosition.toAbsolutePath() + DIR_TO_SAVE_PDF;
    }
}
