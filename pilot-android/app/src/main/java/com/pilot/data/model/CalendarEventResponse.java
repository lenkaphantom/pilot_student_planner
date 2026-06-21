package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Mapira CalendarEventResponse sa backends:
 *   GET /api/calendar/events/upcoming
 *
 * EventType enum vrednosti: EXAM, COLLOQUIUM, PERSONAL, DEADLINE
 */
public class CalendarEventResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    /** "EXAM" | "COLLOQUIUM" | "PERSONAL" | "DEADLINE" */
    @SerializedName("eventType")
    private String eventType;

    /** ISO-8601, npr. "2026-05-26T10:00:00" */
    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("subjectName")
    private String subjectName;

    @SerializedName("createdAt")
    private String createdAt;

    // ── Getteri ──────────────────────────────────────

    public Long getId()           { return id; }
    public String getTitle()      { return title; }
    public String getDescription(){ return description; }
    public String getEventType()  { return eventType; }
    public String getStartTime()  { return startTime; }
    public String getEndTime()    { return endTime; }
    public String getSubjectName(){ return subjectName; }
    public String getCreatedAt()  { return createdAt; }

    /**
     * Vraća broj dana do eventa od danas.
     * Koristi se za boju urgentnosti (crvena/narandžasta/siva).
     */
    public long getDaysUntil() {
        if (startTime == null) return Long.MAX_VALUE;
        try {
            java.time.LocalDateTime eventDate = java.time.LocalDateTime.parse(startTime);
            java.time.LocalDate today = java.time.LocalDate.now();
            return java.time.temporal.ChronoUnit.DAYS.between(today, eventDate.toLocalDate());
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }
}