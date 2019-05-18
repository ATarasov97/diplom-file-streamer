package com.diplom.filestreamer.rest;


import com.diplom.filestreamer.dto.SourceDto;
import com.diplom.filestreamer.entity.Source;
import com.diplom.filestreamer.entity.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache/source")
@RequiredArgsConstructor
public class SourceController {

    private final SourceRepository sourceRepository;

    @PostMapping("/create")
    public void createCacheSource(@RequestBody SourceDto source) {
        sourceRepository.save(Source.builder()
                .id(source.getId())
                .maxCacheAmount(source.getMaxCacheAmount())
                .build());
    }
}
