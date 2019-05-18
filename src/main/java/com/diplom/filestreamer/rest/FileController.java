package com.diplom.filestreamer.rest;

import com.diplom.filestreamer.dto.CacheRequestDto;
import com.diplom.filestreamer.service.CacheService;
import com.diplom.filestreamer.service.DownloadService;
import com.diplom.filestreamer.service.OpenFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class FileController {

    private final DownloadService downloadService;
    private final OpenFileService openFileService;
    private final CacheService cacheService;

    @GetMapping("/file/{name}")
    public ResponseEntity<byte[]> getVideo(@PathVariable String name,
                                           @RequestHeader HttpHeaders headers) throws IOException {
        return downloadService.formResponse(name, headers);
    }

    @GetMapping("/metadata/{name}")
    public ResponseEntity<String> getMetadata(@PathVariable String name) {
        return openFileService.formMetadataResponse(name);
    }

    @PostMapping("/store")
    public ResponseEntity<String> cacheImmediately(@RequestBody CacheRequestDto request) throws IOException {

        return ResponseEntity.ok(
                cacheService.cache(request.getFileId(), request.getSource(), request.getBegin(), request.getEnd()));
    }
}
