package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;

public class LearningStyleRequest {
    @SerializedName("dailyStudyHours")   public final String dailyStudyHours;
    @SerializedName("preferredTime")     public final String preferredTime;
    @SerializedName("reminderDaysAhead") public final int    reminderDaysAhead;

    public LearningStyleRequest(String dailyStudyHours, String preferredTime, int reminderDaysAhead) {
        this.dailyStudyHours   = dailyStudyHours;
        this.preferredTime     = preferredTime;
        this.reminderDaysAhead = reminderDaysAhead;
    }
}
