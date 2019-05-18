package com.diplom.filestreamer.service;

import com.diplom.filestreamer.collector.OpenFileCollector;
import com.diplom.filestreamer.fileserver.service.FileServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenFileService {

    private final FileServerService fileServerService;
    private final List<OpenFileCollector> collectors;

    public ResponseEntity<String> formMetadataResponse(String id) {
        collectors.forEach(collector -> collector.collect(id));
        String fileMetadata = fileServerService.getFileMetadata(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(fileMetadata);

    }
}
