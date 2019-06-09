package com.diplom.filestreamer.additional;

import com.diplom.filestreamer.fileserver.service.FileServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileServerServiceImpl implements FileServerService {

    private final FileServerClient fileServerClient;

    @Override
    public ResponseEntity<Resource> getRowFileInRange(String fileId, HttpRange range) {
        return fileServerClient.getFileInRange(fileId, range);
    }

    @Override
    public String getFileMetadata(String id) {
        return fileServerClient.getFileMetadata(id);
    }

}