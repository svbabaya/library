package ru.maxima.springbootapp.library.util;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.util.Date;
import java.util.TimeZone;

@Getter
@Setter
@RequiredArgsConstructor
public class PersonErrorResponse {

    private String message;
    private Date timestamp;
    private TimeZone timezone;

    public PersonErrorResponse(String message, Date timestamp, TimeZone timezone) {
        this.message = message;
        this.timestamp = timestamp;
        this.timezone = timezone;
    }
}
