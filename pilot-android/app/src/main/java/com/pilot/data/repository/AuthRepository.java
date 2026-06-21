package com.pilot.data.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pilot.data.api.AuthApiService;
import com.pilot.data.api.RetrofitClient;
import com.pilot.data.api.TokenManager;
import com.pilot.data.model.ApiError;
import com.pilot.data.model.AuthResponse;
import com.pilot.data.model.LoginRequest;
import com.pilot.data.model.RegisterRequest;

import java.io.IOException;

import retrofit2.Response;

public class AuthRepository {

    private final AuthApiService apiService;
    private final TokenManager tokenManager;

    public AuthRepository(Context context) {
        apiService = RetrofitClient.getInstance(context).create(AuthApiService.class);
        tokenManager = TokenManager.getInstance(context);
    }

    public Result<AuthResponse> login(String email, String password) {
        try {
            Response<AuthResponse> response = apiService
                    .login(new LoginRequest(email, password))
                    .execute();

            return handleAuthResponse(response);
        } catch (IOException e) {
            Log.e("AuthRepository", "Login greška: " + e.getMessage(), e);
            return Result.error("Nema internet konekcije. Proverite da je backend dostupan na " + com.pilot.BuildConfig.BASE_URL);
        }
    }

    public Result<AuthResponse> register(
            String fullName, String email, String password, String confirmPassword
    ) {
        try {
            Response<AuthResponse> response = apiService
                    .register(new RegisterRequest(fullName, email, password, confirmPassword))
                    .execute();

            return handleAuthResponse(response);
        } catch (IOException e) {
            Log.e("AuthRepository", "Register greška: " + e.getMessage(), e);
            return Result.error("Nema internet konekcije. Proverite da je backend dostupan na " + com.pilot.BuildConfig.BASE_URL);
        }
    }

    public void logout() {
        try {
            apiService.logout().execute();
        } catch (IOException ignored) {
            // Svejedno brišemo lokalne tokene
        } finally {
            tokenManager.clearAll();
        }
    }

    // ---- Pomocne metode ----

    private Result<AuthResponse> handleAuthResponse(Response<AuthResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            AuthResponse body = response.body();
            // Čuvamo tokene bezbedno
            tokenManager.saveTokens(body.accessToken, body.refreshToken);
            if (body.user != null) {
                tokenManager.saveUserInfo(
                        body.user.id,
                        body.user.email,
                        body.user.fullName,
                        body.user.profileComplete
                );
            }
            return Result.success(body);
        }

        // Parsiramo grešku iz backend response body
        String errorMessage = parseError(response);
        return Result.error(errorMessage);
    }

    private String parseError(Response<?> response) {
        if (response.errorBody() == null) {
            return "Neočekivana greška.";
        }
        try {
            String errorJson = response.errorBody().string();
            ApiError apiError = new Gson().fromJson(errorJson, ApiError.class);
            return apiError != null && apiError.message != null
                    ? apiError.message
                    : "Greška " + response.code();
        } catch (IOException e) {
            return "Greška " + response.code();
        }
    }

    // ---- Result wrapper ----

    public static class Result<T> {
        public final T data;
        public final String error;
        public final boolean isSuccess;

        private Result(T data, String error, boolean isSuccess) {
            this.data = data;
            this.error = error;
            this.isSuccess = isSuccess;
        }

        public static <T> Result<T> success(T data) {
            return new Result<>(data, null, true);
        }

        public static <T> Result<T> error(String message) {
            return new Result<>(null, message, false);
        }
    }
}
