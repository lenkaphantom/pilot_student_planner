package com.pilot.dto;

import com.pilot.enums.InternshipUrgency;
import lombok.Data;

@Data
public class OpportunitiesDto {
    private Boolean wantsInternship;
    private InternshipUrgency internshipUrgency;
    private Boolean wantsScholarships;
    private Boolean wantsExchange;
    private Boolean wantsCompetitions;
    private Boolean wantsVolunteering;
    private Boolean wantsStudentOrgs;
}
