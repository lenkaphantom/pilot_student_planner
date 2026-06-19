package com.pilot.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ProfileBasicDto {
    @NotBlank(message = "Fakultet je obavezan.")
    private String faculty;

    @NotBlank(message = "Studijski program je obavezan.")
    private String studyProgram;

    @NotNull(message = "Godina studija je obavezna.")
    @Min(value = 1, message = "Godina mora biti izmedju 1 i 7.")
    @Max(value = 7, message = "Godina mora biti izmedju 1 i 7.")
    private Short yearOfStudy;
}