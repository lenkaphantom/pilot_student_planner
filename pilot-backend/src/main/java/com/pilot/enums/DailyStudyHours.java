package com.pilot.enums;

import lombok.Getter;

@Getter
public enum DailyStudyHours {
    LESS_THAN_ONE("<1h", "Manje od 1 sata"),
    ONE_TO_TWO("1-2h", "1 do 2 sata"),
    TWO_TO_FOUR("2-4h", "2 do 4 sata"),
    MORE_THAN_FOUR("4h+", "Više od 4 sata");

    private final String code;
    private final String display;

    DailyStudyHours(String code, String display) {
        this.code = code;
        this.display = display;
    }

    public static DailyStudyHours fromCode(String code) {
        for (DailyStudyHours hours : DailyStudyHours.values()) {
            if (hours.code.equalsIgnoreCase(code)) {
                return hours;
            }
        }
        throw new IllegalArgumentException("Nepoznata vrednost za dnevne sate: " + code);
    }
}

