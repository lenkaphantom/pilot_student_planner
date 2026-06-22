package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubjectsRequest {
    @SerializedName("subjects")
    public final List<SubjectItem> subjects;

    public SubjectsRequest(List<SubjectItem> subjects) {
        this.subjects = subjects;
    }

    public static class SubjectItem {
        @SerializedName("name") public String name;
        @SerializedName("goal") public String goal;

        public SubjectItem(String name, String goal) {
            this.name = name;
            this.goal = goal;
        }
    }
}
