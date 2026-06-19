package com.pilot.enums;

import lombok.Getter;

@Getter
public enum InternshipUrgency {
    ACTIVE("Da, aktivno", "Aktivno tražim"),
    MAYBE("Mozda", "Razmišljam"),
    NOT_YET("Ne jos", "Ne još");

    private final String code;
    private final String display;

    InternshipUrgency(String code, String display) {
        this.code = code;
        this.display = display;
    }

    public static InternshipUrgency fromCode(String code) {
        for (InternshipUrgency urgency : InternshipUrgency.values()) {
            if (urgency.code.equalsIgnoreCase(code)) {
                return urgency;
            }
        }
        throw new IllegalArgumentException("Nepoznata hitnost stažiranja: " + code);
    }
}

