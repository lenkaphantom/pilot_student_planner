package com.pilot.data.api;

import com.pilot.data.model.InterestsRequest;
import com.pilot.data.model.LearningStyleRequest;
import com.pilot.data.model.OpportunitiesRequest;
import com.pilot.data.model.ProfileBasicRequest;
import com.pilot.data.model.ProfileResponse;
import com.pilot.data.model.SubjectsRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface ProfileApiService {

    @GET("profile/me")
    Call<ProfileResponse> getProfile();

    @PUT("profile/onboarding/step1")
    Call<ProfileResponse> saveStep1(@Body ProfileBasicRequest request);

    @PUT("profile/onboarding/step2")
    Call<ProfileResponse> saveStep2(@Body InterestsRequest request);

    @PUT("profile/onboarding/step3")
    Call<ProfileResponse> saveStep3(@Body SubjectsRequest request);

    @PUT("profile/onboarding/step4")
    Call<ProfileResponse> saveStep4(@Body OpportunitiesRequest request);

    @PUT("profile/onboarding/step5")
    Call<ProfileResponse> saveStep5(@Body LearningStyleRequest request);
}