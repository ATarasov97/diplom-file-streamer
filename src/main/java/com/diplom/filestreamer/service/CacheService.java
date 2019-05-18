package com.diplom.filestreamer.service;

import com.diplom.filestreamer.exception.FileStreamerException;
import com.diplom.filestreamer.fileserver.service.FileServerService;
import com.diplom.filestreamer.properties.FileStreamerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final StoreService storeService;
    private final FileServerService fileServerService;
    private final FileStreamerProperties properties;

    public String cache(String fileId, String source, long begin, long end) throws IOException {
        return cache(fileId, source, begin, end, () -> storeService.findOldest(source));
    }

    @Transactional
    public String cache(String fileId, String source, long begin, long end, Supplier<Optional<String>> cleanUpIdSupplier) throws IOException {
        Long maxCacheAmount = properties.getCacheSource().get(source);

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
        return storeService.createStoreDescriptor(fileId, source, begin, end, fileInRange);
    }
}
