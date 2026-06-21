package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("fullName")
    public final String fullName;

    @SerializedName("email")
    public final String email;

    @SerializedName("password")
    public final String password;

    @SerializedName("confirmPassword")
    public final String confirmPassword;

    public RegisterRequest(String fullName, String email, String password, String confirmPassword) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
