package com.diplom.filestreamer.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
@ConfigurationProperties("file-streamer")
public class FileStreamerProperties {

    private int contentLength = 1024 * 1024;
    private String filesystem = "file:/";
    private String fileServerUri;

    private Map<String,  Long> cacheSource;
}
