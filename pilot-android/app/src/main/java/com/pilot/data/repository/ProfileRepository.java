package com.pilot.data.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.pilot.data.api.ProfileApiService;
import com.pilot.data.api.RetrofitClient;
import com.pilot.data.api.TokenManager;
import com.pilot.data.model.ApiError;
import com.pilot.data.model.InterestsRequest;
import com.pilot.data.model.LearningStyleRequest;
import com.pilot.data.model.OpportunitiesRequest;
import com.pilot.data.model.ProfileBasicRequest;
import com.pilot.data.model.ProfileResponse;
import com.pilot.data.model.SubjectsRequest;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class ProfileRepository {

    private final ProfileApiService api;
    private final TokenManager tokenManager;

    public ProfileRepository(Context context) {
        api = RetrofitClient.getInstance(context).create(ProfileApiService.class);
        tokenManager = TokenManager.getInstance(context);
    }

    public AuthRepository.Result<ProfileResponse> saveStep1(
            String faculty, String studyProgram, int year) {
        return execute(api.saveStep1(new ProfileBasicRequest(faculty, studyProgram, year)));
    }

    public AuthRepository.Result<ProfileResponse> saveStep2(List<String> interests) {
        return execute(api.saveStep2(new InterestsRequest(interests)));
    }

    public AuthRepository.Result<ProfileResponse> saveStep3(List<SubjectsRequest.SubjectItem> subjects) {
        return execute(api.saveStep3(new SubjectsRequest(subjects)));
    }

    public AuthRepository.Result<ProfileResponse> saveStep4(OpportunitiesRequest request) {
        return execute(api.saveStep4(request));
    }

    public AuthRepository.Result<ProfileResponse> saveStep5(
            String dailyStudyHours, String preferredTime, int reminderDaysAhead) {
        return execute(api.saveStep5(
                new LearningStyleRequest(dailyStudyHours, preferredTime, reminderDaysAhead)));
    }

    public void markProfileComplete() {
        tokenManager.markProfileComplete();
    }

    // ---- Generički executor ----

    private <T> AuthRepository.Result<T> execute(retrofit2.Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return AuthRepository.Result.success(response.body());
            }
            return AuthRepository.Result.error(parseError(response));
        } catch (IOException e) {
            return AuthRepository.Result.error("Nema internet konekcije.");
        }
    }

    private String parseError(Response<?> response) {
        if (response.errorBody() == null) return "Neočekivana greška.";
        try {
            ApiError err = new Gson().fromJson(response.errorBody().string(), ApiError.class);
            return (err != null && err.message != null) ? err.message : "Greška " + response.code();
        } catch (IOException e) {
            return "Greška " + response.code();
        }
    }
}
