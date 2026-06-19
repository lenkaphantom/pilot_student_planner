package com.pilot.enums;

import lombok.Getter;

@Getter
public enum  PreferredTime {
    MORNING("Jutro", "06:00 - 12:00"),
    BEFORE_NOON("Prepodne", "09:00 - 12:00"),
    AFTERNOON("Popodne", "12:00 - 18:00"),
    EVENING("Uvece", "18:00 - 23:00");

    private final String display;
    private final String timeRange;

    PreferredTime(String display, String timeRange) {
        this.display = display;
        this.timeRange = timeRange;
    }

    public static PreferredTime fromDisplay(String display) {
        for (PreferredTime time : PreferredTime.values()) {
            if (time.display.equalsIgnoreCase(display)) {
                return time;
            }
        }
        throw new IllegalArgumentException("Nepoznato vrijeme: " + display);
    }
}

