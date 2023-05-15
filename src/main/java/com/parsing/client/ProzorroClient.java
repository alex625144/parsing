package com.parsing.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

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

    public String downloadByUrl(URI uri) {
        String lot = uri.toString().split("/")[4];
        String savePath = "C:/" + lot + ".pdf";
        log.debug("file with URI: " + uri + " was saved.");

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
        return savePath;
    }
}
