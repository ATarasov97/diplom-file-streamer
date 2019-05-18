package com.diplom.filestreamer.service;

import com.diplom.filestreamer.entity.Source;
import com.diplom.filestreamer.entity.SourceRepository;
import com.diplom.filestreamer.exception.FileStreamerException;
import com.diplom.filestreamer.fileserver.service.FileServerService;
import com.diplom.filestreamer.properties.FileStreamerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.diplom.filestreamer.header.ContentRangeParser.getRangeFromHeader;

@Service
@RequiredArgsConstructor
@Transactional
public class CacheService {

    private final StoreService storeService;
    private final FileServerService fileServerService;
    private final SourceRepository sourceRepository;

    public List<String> cache(String fileId, String source, long begin, long end) throws IOException {
        return cache(fileId, source, begin, end, () -> storeService.findOldest(source));
    }

    @Transactional
    public List<String> cache(String fileId, String source, long begin, long end, Supplier<Optional<String>> cleanUpIdSupplier) throws IOException {
        var sourceEntity = sourceRepository.findAndLockById(source)
                .orElseThrow(() -> new FileStreamerException("Source is not found"));
        var maxCacheAmount = sourceEntity.getMaxCacheAmount();


        var newFragmentBegin = begin;
        var cacheIdList = new ArrayList<String>();
        while (newFragmentBegin < end) {
            Pair<String, Long> allocatedFragment =
                    cacheFragment(fileId, source, newFragmentBegin, end, maxCacheAmount, cleanUpIdSupplier);
            String cacheId = allocatedFragment.getFirst();
            newFragmentBegin = allocatedFragment.getSecond();
            cacheIdList.add(cacheId);
        }
        return cacheIdList;
    }

    //todo:replace with dto
    private Pair<String, Long> cacheFragment(String fileId, String source, long begin, long end, long maxCacheAmount, Supplier<Optional<String>> cleanUpIdSupplier) throws IOException {
        long filled = storeService.calculateFilled(source);

        if ((end - begin) > maxCacheAmount) {
            throw new FileStreamerException("Cache region is to big");
        }
        while ((end - begin) + filled > maxCacheAmount) {
            Optional<String> optionalId = cleanUpIdSupplier.get();
            if (optionalId.isEmpty()) {
                throw new FileStreamerException("Could not allocate cache");
            }

            storeService.cleanUp(optionalId.get());
            filled = storeService.calculateFilled(source);
        }

        ResponseEntity<Resource> fileInRange = fileServerService.getRowFileInRange(
                fileId,
                HttpRange.createByteRange(begin, end));
        var contentRange = Objects.requireNonNull(fileInRange.getHeaders().get(HttpHeaders.CONTENT_RANGE)).get(0);
        var rangeFromHeader = getRangeFromHeader(contentRange);
        var receivedEndByte = Long.parseLong(rangeFromHeader.getSecond());
        return storeService.createStoreDescriptor(fileId, source, begin, receivedEndByte, fileInRange);
    }
}
