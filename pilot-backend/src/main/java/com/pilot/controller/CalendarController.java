package com.pilot.controller;

import com.pilot.dto.CalendarEventRequest;
import com.pilot.dto.CalendarEventResponse;
import com.pilot.dto.CalendarMonthResponse;
import com.pilot.model.User;
import com.pilot.service.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    /**
     * GET /api/calendar/events?year=2026&month=6
     * Vraca sve dogadjaje korisnika za dati mesec.
     */
    @GetMapping("/events")
    public ResponseEntity<CalendarMonthResponse> getEventsByMonth(
            @AuthenticationPrincipal User user,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(calendarService.getEventsByMonth(user, year, month));
    }

    /**
     * GET /api/calendar/events/upcoming
     * Vraca dogadjaje u narednih 7 dana.
     */
    @GetMapping("/events/upcoming")
    public ResponseEntity<List<CalendarEventResponse>> getUpcoming(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(calendarService.getUpcomingEvents(user));
    }

    /**
     * POST /api/calendar/events
     * Kreira novi dogadjaj (ispit, kolokvijum, licna obaveza).
     */
    @PostMapping("/events")
    public ResponseEntity<CalendarEventResponse> createEvent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CalendarEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calendarService.createEvent(user, request));
    }

    /**
     * PUT /api/calendar/events/{id}
     * Azurira postojeci dogadjaj.
     */
    @PutMapping("/events/{id}")
    public ResponseEntity<CalendarEventResponse> updateEvent(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody CalendarEventRequest request) {
        return ResponseEntity.ok(calendarService.updateEvent(user, id, request));
    }

    /**
     * DELETE /api/calendar/events/{id}
     * Brise dogadjaj.
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        calendarService.deleteEvent(user, id);
        return ResponseEntity.noContent().build();
    }
}
