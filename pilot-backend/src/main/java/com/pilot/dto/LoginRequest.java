package com.pilot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email je obavezan.")
    @Email(message = "Email nije u ispravnom formatu.")
    private String email;

    @NotBlank(message = "Lozinka je obavezna.")
    private String password;
}
