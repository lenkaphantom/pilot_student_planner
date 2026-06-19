package com.pilot.service;

import com.pilot.dto.CalendarEventRequest;
import com.pilot.dto.CalendarEventResponse;
import com.pilot.dto.CalendarMonthResponse;
import com.pilot.exception.ResourceNotFoundException;
import com.pilot.model.CalendarEvent;
import com.pilot.model.StudentSubject;
import com.pilot.model.User;
import com.pilot.repository.CalendarEventRepository;
import com.pilot.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarEventRepository eventRepository;
    private final StudentProfileRepository profileRepository;

    @Transactional
    public CalendarEventResponse createEvent(User user, CalendarEventRequest request) {
        CalendarEvent event = CalendarEvent.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .eventType(request.getEventType())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        // Povezi sa predmetom ako je prosledjen
        if (request.getSubjectId() != null) {
            StudentSubject subject = findSubjectById(user, request.getSubjectId());
            event.setSubject(subject);
        }

        return mapToResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public CalendarMonthResponse getEventsByMonth(User user, int year, int month) {
        List<CalendarEventResponse> events = eventRepository
                .findByUserIdAndYearAndMonth(user.getId(), year, month)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        CalendarMonthResponse response = new CalendarMonthResponse();
        response.setYear(year);
        response.setMonth(month);
        response.setEvents(events);
        return response;
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getUpcomingEvents(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAhead = now.plusWeeks(1);
        return eventRepository
                .findByUserIdAndStartTimeBetween(user.getId(), now, weekAhead)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CalendarEventResponse updateEvent(User user, Long eventId, CalendarEventRequest request) {
        CalendarEvent event = getEventOrThrow(eventId, user);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventType(request.getEventType());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());

        if (request.getSubjectId() != null) {
            event.setSubject(findSubjectById(user, request.getSubjectId()));
        } else {
            event.setSubject(null);
        }

        return mapToResponse(eventRepository.save(event));
    }

    @Transactional
    public void deleteEvent(User user, Long eventId) {
        CalendarEvent event = getEventOrThrow(eventId, user);
        eventRepository.delete(event);
    }

    // ---- Pomocne metode ----

    private CalendarEvent getEventOrThrow(Long eventId, User user) {
        CalendarEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Dogadjaj sa ID " + eventId + " nije pronadjen."));
        if (!event.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Nemate pristup ovom dogadjaju.");
        }
        return event;
    }

    private StudentSubject findSubjectById(User user, Long subjectId) {
        return profileRepository.findByUserId(user.getId())
                .flatMap(p -> p.getSubjects().stream()
                        .filter(s -> s.getId().equals(subjectId))
                        .findFirst())
                .orElseThrow(() -> new ResourceNotFoundException("Predmet sa ID " + subjectId + " nije pronadjen."));
    }

    private CalendarEventResponse mapToResponse(CalendarEvent e) {
        CalendarEventResponse r = new CalendarEventResponse();
        r.setId(e.getId());
        r.setTitle(e.getTitle());
        r.setDescription(e.getDescription());
        r.setEventType(e.getEventType());
        r.setStartTime(e.getStartTime());
        r.setEndTime(e.getEndTime());
        r.setSubjectName(e.getSubject() != null ? e.getSubject().getName() : null);
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }
}
