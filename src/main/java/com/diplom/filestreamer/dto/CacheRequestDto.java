package com.diplom.filestreamer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CacheRequestDto {
    private String fileId;
    private String source;
    private long begin;
    private long end;
}
