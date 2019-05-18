package com.diplom.filestreamer.additional;

import com.diplom.filestreamer.dto.FilePartDto;
import com.diplom.filestreamer.fileserver.service.FileServerService;
import lombok.RequiredArgsConstructor;
import org.h2.util.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.diplom.filestreamer.header.ContentRangeParser.getRangeFromHeader;

@Service
@RequiredArgsConstructor
public class FileServerServiceImpl implements FileServerService {

    private final FileServerClient fileServerClient;

    @Override
    public FilePartDto getFileInRange(String fileId, HttpRange range) throws IOException {
        var response = fileServerClient.getFileInRange(fileId, range);
        var out = new ByteArrayOutputStream();
        IOUtils.copy(Objects.requireNonNull(response.getBody()).getInputStream(), out);
        out.close();
        var contentRange = Objects.requireNonNull(response.getHeaders().get(HttpHeaders.CONTENT_RANGE)).get(0);
        Pair<String, String> rangePair = getRangeFromHeader(contentRange);

        return FilePartDto.builder()
                .mediaType(response.getHeaders().getContentType())
                .out(out.toByteArray())
                .contentRange(contentRange)
                .begin(Long.parseLong(rangePair.getFirst()))
                .end(Long.parseLong(rangePair.getSecond()))
                .build();
    }

    @Override
    public ResponseEntity<Resource> getRowFileInRange(String fileId, HttpRange range) {
        return fileServerClient.getFileInRange(fileId, range);
    }

    @Override
    public String getFileMetadata(String id) {
        return fileServerClient.getFileMetadata(id);
    }

}