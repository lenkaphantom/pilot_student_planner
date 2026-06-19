package com.pilot.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private boolean profileComplete;
}