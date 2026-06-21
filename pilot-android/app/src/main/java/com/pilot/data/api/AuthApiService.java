package com.pilot.data.api;

import com.pilot.data.model.AuthResponse;
import com.pilot.data.model.LoginRequest;
import com.pilot.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("auth/logout")
    Call<Void> logout();
}
