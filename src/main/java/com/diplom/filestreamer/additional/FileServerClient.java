package com.diplom.filestreamer.additional;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Collections.emptyMap;

@Service
@AllArgsConstructor
public class FileServerClient {

    //TODO: configurable
    private static final String GET_FILE = "/file/%s";
    private static final String GET_METADATA = "/metadata/%s";

    @Qualifier("fileServerRestTemplate")
    private final RestTemplate restTemplate;

    public ResponseEntity<Resource> getFileInRange(String id, HttpRange range) {
        HttpHeaders headers = new HttpHeaders();
        headers.setRange(List.of(range));
        var entity = new HttpEntity(headers);
        return restTemplate.exchange(String.format(GET_FILE, id), HttpMethod.GET, entity, Resource.class, emptyMap());
    }

    public String getFileMetadata(String id) {
        return restTemplate.exchange(String.format(GET_METADATA, id), HttpMethod.GET, null, String.class, emptyMap()).getBody();
    }
}
