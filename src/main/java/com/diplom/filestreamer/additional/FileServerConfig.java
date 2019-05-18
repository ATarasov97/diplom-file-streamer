package com.diplom.filestreamer.additional;

import com.diplom.filestreamer.properties.FileStreamerProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FileServerConfig {

    @Bean(name = "fileServerRestTemplate")
    public RestTemplate fileServerRestTemplate(RestTemplateBuilder builder, FileStreamerProperties properties) {
        return builder
                .rootUri(properties.getFileServerUri())
                .build();
    }
}
