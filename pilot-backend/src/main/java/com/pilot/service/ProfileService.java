package com.pilot.service;

import com.pilot.dto.*;
import com.pilot.exception.ResourceNotFoundException;
import com.pilot.model.StudentInterest;
import com.pilot.model.StudentProfile;
import com.pilot.model.StudentSubject;
import com.pilot.model.User;
import com.pilot.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final StudentProfileRepository profileRepository;

    // ---- Korak 1/5: Osnovni podaci ----

    @Transactional
    public ProfileResponse saveBasicInfo(User user, ProfileBasicDto dto) {
        StudentProfile profile = getOrCreateProfile(user);
        profile.setFaculty(dto.getFaculty());
        profile.setStudyProgram(dto.getStudyProgram());
        profile.setYearOfStudy(dto.getYearOfStudy());
        return mapToResponse(profileRepository.save(profile));
    }

    // ---- Korak 2/5: Interesovanja ----

    @Transactional
    public ProfileResponse saveInterests(User user, InterestsDto dto) {
        StudentProfile profile = getOrCreateProfile(user);
        profile.getInterests().clear();
        dto.getInterests().forEach(i -> {
            StudentInterest interest = new StudentInterest();
            interest.setProfile(profile);
            interest.setInterest(i);
            profile.getInterests().add(interest);
        });
        return mapToResponse(profileRepository.save(profile));
    }

    // ---- Korak 3/5: Predmeti ----

    @Transactional
    public ProfileResponse saveSubjects(User user, SubjectsDto dto) {
        StudentProfile profile = getOrCreateProfile(user);
        profile.getSubjects().clear();
        dto.getSubjects().forEach(s -> {
            StudentSubject subject = new StudentSubject();
            subject.setProfile(profile);
            subject.setName(s.getName());
            subject.setGoal(s.getGoal());
            profile.getSubjects().add(subject);
        });
        return mapToResponse(profileRepository.save(profile));
    }

    // ---- Korak 4/5: Prilike ----

    @Transactional
    public ProfileResponse saveOpportunities(User user, OpportunitiesDto dto) {
        StudentProfile profile = getOrCreateProfile(user);
        if (dto.getWantsInternship() != null) profile.setWantsInternship(dto.getWantsInternship());
        if (dto.getInternshipUrgency() != null) profile.setInternshipUrgency(dto.getInternshipUrgency());
        if (dto.getWantsScholarships() != null) profile.setWantsScholarships(dto.getWantsScholarships());
        if (dto.getWantsExchange() != null) profile.setWantsExchange(dto.getWantsExchange());
        if (dto.getWantsCompetitions() != null) profile.setWantsCompetitions(dto.getWantsCompetitions());
        if (dto.getWantsVolunteering() != null) profile.setWantsVolunteering(dto.getWantsVolunteering());
        if (dto.getWantsStudentOrgs() != null) profile.setWantsStudentOrgs(dto.getWantsStudentOrgs());
        return mapToResponse(profileRepository.save(profile));
    }

    // ---- Korak 5/5: Stil ucenja (zavrsava onboarding) ----

    @Transactional
    public ProfileResponse saveLearningStyle(User user, LearningStyleDto dto) {
        StudentProfile profile = getOrCreateProfile(user);
        profile.setDailyStudyHours(dto.getDailyStudyHours());
        profile.setPreferredTime(dto.getPreferredTime());
        profile.setReminderDaysAhead(dto.getReminderDaysAhead());
        profile.setProfileComplete(true);  // onboarding zavrsen
        return mapToResponse(profileRepository.save(profile));
    }

    // ---- GET profila ----

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(User user) {
        StudentProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil nije pronadjen."));
        return mapToResponse(profile);
    }

    // ---- Pomocne metode ----

    private StudentProfile getOrCreateProfile(User user) {
        return profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    StudentProfile p = StudentProfile.builder().user(user).build();
                    return profileRepository.save(p);
                });
    }

    private ProfileResponse mapToResponse(StudentProfile p) {
        ProfileResponse r = new ProfileResponse();
        r.setId(p.getId());
        r.setFaculty(p.getFaculty());
        r.setStudyProgram(p.getStudyProgram());
        r.setYearOfStudy(p.getYearOfStudy());
        r.setInterests(p.getInterests().stream()
                .map(StudentInterest::getInterest)
                .collect(Collectors.toList()));
        r.setSubjects(p.getSubjects().stream()
                .map(s -> {
                    SubjectsDto.SubjectItem item = new SubjectsDto.SubjectItem();
                    item.setName(s.getName());
                    item.setGoal(s.getGoal());
                    return item;
                })
                .collect(Collectors.toList()));
        r.setWantsInternship(p.getWantsInternship());
        r.setInternshipUrgency(p.getInternshipUrgency());
        r.setWantsScholarships(p.getWantsScholarships());
        r.setWantsExchange(p.getWantsExchange());
        r.setWantsCompetitions(p.getWantsCompetitions());
        r.setWantsVolunteering(p.getWantsVolunteering());
        r.setWantsStudentOrgs(p.getWantsStudentOrgs());
        r.setDailyStudyHours(p.getDailyStudyHours());
        r.setPreferredTime(p.getPreferredTime());
        r.setReminderDaysAhead(p.getReminderDaysAhead());
        r.setProfileComplete(p.getProfileComplete());
        return r;
    }
}
