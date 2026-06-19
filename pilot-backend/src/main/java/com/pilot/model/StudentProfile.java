package com.pilot.model;

import com.pilot.enums.DailyStudyHours;
import com.pilot.enums.InternshipUrgency;
import com.pilot.enums.PreferredTime;
import com.pilot.enums.ReminderDaysAhead;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String faculty;

    @Column(name = "study_program")
    private String studyProgram;

    @Column(name = "year_of_study")
    private Short yearOfStudy;

    // Stil ucenja (Korak 5/5)
    @Column(name = "daily_study_hours")
    @Enumerated(EnumType.STRING)
    private DailyStudyHours dailyStudyHours;

    @Column(name = "preferred_time")
    @Enumerated(EnumType.STRING)
    private PreferredTime preferredTime;

    @Column(name = "reminder_days_ahead")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReminderDaysAhead reminderDaysAhead = ReminderDaysAhead.THREE_DAYS;

    // Prilike (Korak 4/5)
    @Column(name = "wants_internship")
    @Builder.Default
    private Boolean wantsInternship = false;

    @Column(name = "internship_urgency")
    @Enumerated(EnumType.STRING)
    private InternshipUrgency internshipUrgency;

    @Column(name = "wants_scholarships")
    @Builder.Default
    private Boolean wantsScholarships = false;

    @Column(name = "wants_exchange")
    @Builder.Default
    private Boolean wantsExchange = false;

    @Column(name = "wants_competitions")
    @Builder.Default
    private Boolean wantsCompetitions = false;

    @Column(name = "wants_volunteering")
    @Builder.Default
    private Boolean wantsVolunteering = false;

    @Column(name = "wants_student_orgs")
    @Builder.Default
    private Boolean wantsStudentOrgs = false;

    @Column(name = "profile_complete")
    @Builder.Default
    private Boolean profileComplete = false;

    // Interesovanja (Korak 2/5)
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudentInterest> interests = new ArrayList<>();

    // Predmeti (Korak 3/5)
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudentSubject> subjects = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
