package com.diplom.filestreamer.fileserver.service;

import com.diplom.filestreamer.dto.FilePartDto;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.OutputStream;

public interface FileServerService {
    FilePartDto getFileInRange(String fileId, HttpRange range) throws IOException;

    ResponseEntity<Resource> getRowFileInRange(String fileId, HttpRange range);

    String getFileMetadata(String id);
}
