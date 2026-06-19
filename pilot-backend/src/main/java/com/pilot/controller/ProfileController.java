package com.pilot.controller;

import com.pilot.dto.*;
import com.pilot.model.User;
import com.pilot.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * GET /api/profile/me
     * Vraca kompletan profil trenutno ulogovanog korisnika.
     */
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getProfile(user));
    }

    /**
     * PUT /api/profile/onboarding/step1
     * Korak 1/5: fakultet, smer, godina studija
     */
    @PutMapping("/onboarding/step1")
    public ResponseEntity<ProfileResponse> saveStep1(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileBasicDto dto) {
        return ResponseEntity.ok(profileService.saveBasicInfo(user, dto));
    }

    /**
     * PUT /api/profile/onboarding/step2
     * Korak 2/5: interesovanja (Web razvoj, AI/ML, itd.)
     */
    @PutMapping("/onboarding/step2")
    public ResponseEntity<ProfileResponse> saveStep2(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody InterestsDto dto) {
        return ResponseEntity.ok(profileService.saveInterests(user, dto));
    }

    /**
     * PUT /api/profile/onboarding/step3
     * Korak 3/5: predmeti sa ciljevima
     */
    @PutMapping("/onboarding/step3")
    public ResponseEntity<ProfileResponse> saveStep3(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubjectsDto dto) {
        return ResponseEntity.ok(profileService.saveSubjects(user, dto));
    }

    /**
     * PUT /api/profile/onboarding/step4
     * Korak 4/5: prilike (prakse, stipendije, razmene...)
     */
    @PutMapping("/onboarding/step4")
    public ResponseEntity<ProfileResponse> saveStep4(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody OpportunitiesDto dto) {
        return ResponseEntity.ok(profileService.saveOpportunities(user, dto));
    }

    /**
     * PUT /api/profile/onboarding/step5
     * Korak 5/5: stil ucenja — zatvara onboarding, setuje profileComplete = true
     */
    @PutMapping("/onboarding/step5")
    public ResponseEntity<ProfileResponse> saveStep5(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody LearningStyleDto dto) {
        return ResponseEntity.ok(profileService.saveLearningStyle(user, dto));
    }
}
