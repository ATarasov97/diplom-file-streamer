package com.diplom.filestreamer.fileserver.service;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;

public interface FileServerService {
    ResponseEntity<Resource> getRowFileInRange(String fileId, HttpRange range);

    String getFileMetadata(String id);
}
