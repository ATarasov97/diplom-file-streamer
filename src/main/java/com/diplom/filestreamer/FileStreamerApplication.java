package com.diplom.filestreamer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FileStreamerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileStreamerApplication.class, args);
    }
}
