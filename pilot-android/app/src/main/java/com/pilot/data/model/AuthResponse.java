package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("accessToken")
    public String accessToken;

    @SerializedName("refreshToken")
    public String refreshToken;

    @SerializedName("tokenType")
    public String tokenType;

    @SerializedName("expiresIn")
    public long expiresIn;

    @SerializedName("user")
    public UserInfo user;

    public static class UserInfo {
        @SerializedName("id")
        public long id;

        @SerializedName("email")
        public String email;

        @SerializedName("fullName")
        public String fullName;

        @SerializedName("profileComplete")
        public boolean profileComplete;
    }
}
