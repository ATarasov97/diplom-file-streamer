package com.diplom.filestreamer.service;

import com.diplom.filestreamer.collector.DownloadCollector;
import com.diplom.filestreamer.dto.FilePartDto;
import com.diplom.filestreamer.fileserver.service.FileServerService;
import lombok.RequiredArgsConstructor;
import org.h2.util.IOUtils;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.diplom.filestreamer.header.ContentRangeParser.getRangeFromHeader;

@Service
@RequiredArgsConstructor
public class DownloadService {

    private final StoreService storeService;
    private final FileServerService fileServerService;
    private final List<DownloadCollector> collectors;

    public ResponseEntity<byte[]> formResponse(String fileId, HttpHeaders headers) throws IOException {
        var range = headers.getRange().get(0);
        FilePartDto filePart = storeService.findCache(fileId, range)
                .orElse(getFileInRange(fileId, range));

        collectors.forEach(collector -> collector.collect(fileId, filePart.getBegin(), filePart.getEnd()));

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_RANGE, filePart.getContentRange())
                .contentType(filePart.getMediaType())
                .body(filePart.getOut());
    }

    private FilePartDto getFileInRange(String fileId, HttpRange range) throws IOException {
        var response = fileServerService.getRowFileInRange(fileId, range);
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
//    public static void main(String[] args) throws IOException {
//        var urlResource = new UrlResource("file:/home/alexandr/diplom/file-streamer/src/main/resources/videos/ricardo.mp4");
//
//        InputStream inputStream = urlResource.getInputStream();
//        FileOutputStream fileOutputStream = new FileOutputStream("/home/alexandr/diplom/file-streamer/src/main/resources/store/2");
////        StreamUtils.copyRange(inputStream, fileOutputStream, 1024*1024, 1024*1024 * 3);
//        StreamUtils.copyRange(inputStream, fileOutputStream, 1024 * 1024, 1024 * 1000 * 3);
//        fileOutputStream.close();
//    }

}
