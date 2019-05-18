package com.diplom.filestreamer.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Data
@Builder
public class FilePartDto {
    private final MediaType mediaType;
    private final byte[] out;
    private final String contentRange;
    private final long begin;
    private final long end;
}
