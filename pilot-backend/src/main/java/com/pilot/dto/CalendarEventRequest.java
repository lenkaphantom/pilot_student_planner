package com.pilot.dto;

import com.pilot.model.CalendarEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarEventRequest {
    @NotBlank(message = "Naziv dogadjaja je obavezan.")
    private String title;

    private String description;

    @NotNull(message = "Tip dogadjaja je obavezan.")
    private CalendarEvent.EventType eventType;

    @NotNull(message = "Vreme pocetka je obavezno.")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long subjectId;
}
