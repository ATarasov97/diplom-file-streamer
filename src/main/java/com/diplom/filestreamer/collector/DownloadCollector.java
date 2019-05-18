package com.diplom.filestreamer.collector;

public interface DownloadCollector {
    void collect(String id, long begin, long end);
}
