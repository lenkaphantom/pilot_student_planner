package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProfileBasicRequest {
    @SerializedName("faculty")      public final String faculty;
    @SerializedName("studyProgram") public final String studyProgram;
    @SerializedName("yearOfStudy")  public final int yearOfStudy;

    public ProfileBasicRequest(String faculty, String studyProgram, int yearOfStudy) {
        this.faculty = faculty;
        this.studyProgram = studyProgram;
        this.yearOfStudy = yearOfStudy;
    }
}