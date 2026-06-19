package com.pilot.dto;

import com.pilot.enums.DailyStudyHours;
import com.pilot.enums.InternshipUrgency;
import com.pilot.enums.PreferredTime;
import com.pilot.enums.ReminderDaysAhead;
import lombok.Data;

import java.util.List;

@Data
public class ProfileResponse {
    private Long id;
    private String faculty;
    private String studyProgram;
    private Short yearOfStudy;
    private List<String> interests;
    private List<SubjectsDto.SubjectItem> subjects;
    private Boolean wantsInternship;
    private InternshipUrgency internshipUrgency;
    private Boolean wantsScholarships;
    private Boolean wantsExchange;
    private Boolean wantsCompetitions;
    private Boolean wantsVolunteering;
    private Boolean wantsStudentOrgs;
    private DailyStudyHours dailyStudyHours;
    private PreferredTime preferredTime;
    private ReminderDaysAhead reminderDaysAhead;
    private Boolean profileComplete;
}
