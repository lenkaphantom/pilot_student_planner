package com.pilot.dto;

import lombok.Data;

import java.util.List;

@Data
public class CalendarMonthResponse {
    private int year;
    private int month;
    private List<CalendarEventResponse> events;
}
