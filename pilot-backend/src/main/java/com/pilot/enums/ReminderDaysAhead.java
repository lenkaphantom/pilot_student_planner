package com.pilot.enums;

import lombok.Getter;

@Getter
public enum ReminderDaysAhead {
    ONE_DAY((short) 1, "1 dan"),
    THREE_DAYS((short) 3, "3 dana"),
    SEVEN_DAYS((short) 7, "7 dana"),
    FOURTEEN_DAYS((short) 14, "14 dana");

    private final Short days;
    private final String display;

    ReminderDaysAhead(Short days, String display) {
        this.days = days;
        this.display = display;
    }

    public static ReminderDaysAhead fromDays(Short days) {
        for (ReminderDaysAhead reminder : ReminderDaysAhead.values()) {
            if (reminder.days.equals(days)) {
                return reminder;
            }
        }
        throw new IllegalArgumentException("Nepoznat broj dana: " + days);
    }
}

