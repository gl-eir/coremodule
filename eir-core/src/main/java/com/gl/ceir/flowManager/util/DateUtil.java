package com.gl.ceir.flowManager.util;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DateUtil {

    DateTimeFormatter URL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public static DateTimeFormatter defaultDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public LocalDateTime getCurrentDate() {
        return LocalDateTime.now();
    }

    public LocalDateTime fromUrlFormat(String requestDate) {
        return LocalDateTime.parse(requestDate, URL_DATE_FORMATTER);
    }
}
