package com.pilot.repository;

import com.pilot.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByUserIdOrderByStartTimeAsc(Long userId);

    @Query("""
        SELECT e FROM CalendarEvent e
        WHERE e.user.id = :userId
          AND YEAR(e.startTime) = :year
          AND MONTH(e.startTime) = :month
        ORDER BY e.startTime ASC
    """)
    List<CalendarEvent> findByUserIdAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Query("""
        SELECT e FROM CalendarEvent e
        WHERE e.user.id = :userId
          AND e.startTime BETWEEN :from AND :to
        ORDER BY e.startTime ASC
    """)
    List<CalendarEvent> findByUserIdAndStartTimeBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
