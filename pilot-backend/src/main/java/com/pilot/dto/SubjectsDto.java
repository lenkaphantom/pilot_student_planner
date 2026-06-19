package com.pilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubjectsDto {
    @NotNull
    private List<SubjectItem> subjects;

    @Data
    public static class SubjectItem {
        @NotBlank
        private String name;
        private String goal;
    }
}
