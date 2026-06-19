package com.pilot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InterestsDto {
    @NotNull(message = "List interesovanja je obavezan")
    private List<String> interests;
}
