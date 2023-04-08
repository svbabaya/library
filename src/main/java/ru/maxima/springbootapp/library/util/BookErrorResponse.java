package ru.maxima.springbootapp.library.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.TimeZone;

@Getter
@Setter
@RequiredArgsConstructor
public class BookErrorResponse {

    private String message;
    private Date timestamp;
    private TimeZone timezone;

    public BookErrorResponse(String message, Date timestamp, TimeZone timezone) {
        this.message = message;
        this.timestamp = timestamp;
        this.timezone = timezone;
    }
}
