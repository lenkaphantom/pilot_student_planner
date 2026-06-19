package com.pilot.dto;

import com.pilot.enums.DailyStudyHours;
import com.pilot.enums.PreferredTime;
import com.pilot.enums.ReminderDaysAhead;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LearningStyleDto {
    @NotNull(message = "Dnevne sate učenja je obavezno specificirati")
    private DailyStudyHours dailyStudyHours;

    @NotNull(message = "Prepoznano vrijeme je obavezno specificirati")
    private PreferredTime preferredTime;

    @NotNull(message = "Broj dana za reminiscenciju je obavezan")
    private ReminderDaysAhead reminderDaysAhead;
}
