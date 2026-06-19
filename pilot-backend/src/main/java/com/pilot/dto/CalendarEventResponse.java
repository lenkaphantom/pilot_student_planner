package com.pilot.dto;

import com.pilot.model.CalendarEvent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarEventResponse {
    private Long id;
    private String title;
    private String description;
    private CalendarEvent.EventType eventType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String subjectName;
    private LocalDateTime createdAt;
}
