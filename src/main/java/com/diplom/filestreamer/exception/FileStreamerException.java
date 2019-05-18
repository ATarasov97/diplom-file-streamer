package com.diplom.filestreamer.exception;

public class FileStreamerException extends RuntimeException {

    public FileStreamerException() {
        super();
    }

    public FileStreamerException(String message) {
        super(message);
    }

    public FileStreamerException(Throwable cause) {
        super(cause);
    }

    public FileStreamerException(String message, Throwable cause) {
        super(message, cause);
    }
}
