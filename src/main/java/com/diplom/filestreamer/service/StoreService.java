package com.diplom.filestreamer.service;

import com.diplom.filestreamer.dto.FilePartDto;
import com.diplom.filestreamer.entity.StoreDescription;
import com.diplom.filestreamer.entity.StoreDescriptionRepository;
import com.diplom.filestreamer.properties.FileStreamerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static com.diplom.filestreamer.header.ContentRangeParser.getContentLength;
import static com.diplom.filestreamer.header.ContentRangeParser.getRangeFromHeader;
import static java.lang.Math.min;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final FileStreamerProperties properties;
    private final StoreDescriptionRepository storeDescriptionRepository;

    @Transactional
    public Optional<FilePartDto> findCache(String fileId, HttpRange range) {

        var descriptionList = storeDescriptionRepository.findAllByFileIdAndFileNameIsNotNull(fileId);
        if (descriptionList.isEmpty()) {
            return Optional.empty();
        }

        var contentLength = descriptionList.get(0).getContentLength();
        var begin = 0L;
        var end = min(properties.getContentLength(), contentLength);
        ;

        if (range != null) {
            begin = range.getRangeStart(contentLength);
            end = begin + min(properties.getContentLength(), range.getRangeEnd(contentLength) - begin + 1);
        }
        var finalBegin = begin;
        var finalEnd = end;

        var cacheMetadata = descriptionList
                .stream()
                .filter(sd -> sd.getStartByte() <= finalBegin && sd.getEndByte() >= finalEnd)
                .findFirst();

        return cacheMetadata.map(cache -> {
            var fileUrl = String.format("file:%s/%s", properties.getFilesystem(), cache.getFileName());
            try {
                var file = new UrlResource(fileUrl);
                var out = new ByteArrayOutputStream();
                StreamUtils.copyRange(
                        file.getInputStream(),
                        out,
                        finalBegin - cache.getStartByte(),
                        finalEnd - cache.getStartByte() - 1
                );
                out.close();
                return FilePartDto.builder()
                        .out(out.toByteArray())
                        .mediaType(MediaType.parseMediaType(cache.getMediaType()))
                        .contentRange(String.format("bytes %s-%s/%s", finalBegin, finalEnd, contentLength))
                        .begin(finalBegin)
                        .end(finalEnd)
                        .build();
            } catch (IOException e) {
                //todo: logger
                System.out.println("Cannot find cache file");
                return null;
            }
        });
    }

    public long calculateFilled(String source) {
        return storeDescriptionRepository.calculateFilledSpace(source).orElse(0L);
    }

    public Optional<String> findOldest(String source) {
        return storeDescriptionRepository.findFirstBySourceOrderByCreationDate(source)
                .map(StoreDescription::getId);
    }

    @Transactional
    public void cleanUp(String id) {
        deleteFragment(storeDescriptionRepository.getOne(id));
    }

    @Transactional
    public Pair<String, Long> createStoreDescriptor(String fileId,
                                                    String source,
                                                    long begin,
                                                    long end,
                                                    ResponseEntity<Resource> resource) throws IOException {
        var greaterFragmentOptional = storeDescriptionRepository.findFirstByStartByteLessThanEqualAndEndByteGreaterThanEqual(begin, end);
        if (greaterFragmentOptional.isPresent()) {
            StoreDescription greaterFragment = greaterFragmentOptional.get();
            return Pair.of(greaterFragment.getId(), greaterFragment.getEndByte());
        }
        var smallerFragmentOptional = storeDescriptionRepository.findFirstByStartByteGreaterThanEqualAndEndByteLessThanEqual(begin, end);
        var newFragment = createNewFragment(fileId, source, begin, end, resource);
        smallerFragmentOptional.ifPresent(this::deleteFragment);
        return newFragment;
    }

    private void deleteFragment(StoreDescription storeDescription) {
        var fileName = storeDescription.getFileName();
        var fileUrl = String.format("%s/%s", properties.getFilesystem(), fileName);
        storeDescriptionRepository.delete(storeDescription);
        var file = new File(fileUrl);
        file.delete();
    }

    private Pair<String, Long> createNewFragment(String fileId, String source, long begin, long end, ResponseEntity<Resource> resource) throws IOException {
        var contentRange = Objects.requireNonNull(resource.getHeaders().get(HttpHeaders.CONTENT_RANGE)).get(0);
        var newStoreDescriptor = StoreDescription.builder()
                .fileId(fileId)
                .contentLength(Long.parseLong(getContentLength(contentRange)))
                .creationDate(Instant.now())
                .mediaType(resource.getHeaders().getContentType().toString())
                .source(source)
                .startByte(begin)
                .endByte(end)
                .build();
        storeDescriptionRepository.save(newStoreDescriptor);
        storeDescriptionRepository.flush();
        InputStream is = resource.getBody().getInputStream();
        var id = newStoreDescriptor.getId();
        var fileUrl = String.format("%s/%s", properties.getFilesystem(), id);
        FileOutputStream fos = new FileOutputStream(fileUrl);
        StreamUtils.copy(is, fos);
        fos.close();
        newStoreDescriptor.setFileName(id);
        return Pair.of(id, end);
    }

}
