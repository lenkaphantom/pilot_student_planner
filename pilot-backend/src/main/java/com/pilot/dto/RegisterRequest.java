package com.pilot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Ime i prezime su obavezni.")
    @Size(min = 2, max = 255, message = "Ime mora imati izmedju 2 i 255 karaktera.")
    private String fullName;

    @NotBlank(message = "Email je obavezan.")
    @Email(message = "Email nije u ispravnom formatu.")
    private String email;

    @NotBlank(message = "Lozinka je obavezna.")
    @Size(min = 8, message = "Lozinka mora imati najmanje 8 karaktera.")
    private String password;

    @NotBlank(message = "Potvrda lozinke je obavezna.")
    private String confirmPassword;
}
