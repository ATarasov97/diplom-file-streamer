package com.diplom.filestreamer.header;

import org.springframework.data.util.Pair;

import java.util.regex.Pattern;

//todo: replace with factory
public class ContentRangeParser {
    private static final Pattern PATTERN = Pattern.compile(".* ([0-9]+)-([0-9]+)/([0-9]+)");

    public static Pair<String, String> getRangeFromHeader(String contentRange) {
        var matcher = PATTERN.matcher(contentRange);
        matcher.find();
        return Pair.of(matcher.group(1), matcher.group(2));
    }

    public static String getContentLength(String contentRange) {
        var matcher = PATTERN.matcher(contentRange);
        matcher.find();
        return matcher.group(3);
    }

}
