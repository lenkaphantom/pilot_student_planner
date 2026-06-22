package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProfileResponse {
    @SerializedName("id")                public long    id;
    @SerializedName("faculty")           public String  faculty;
    @SerializedName("studyProgram")      public String  studyProgram;
    @SerializedName("yearOfStudy")       public int     yearOfStudy;
    @SerializedName("interests")         public List<String> interests;
    @SerializedName("subjects")          public List<SubjectsRequest.SubjectItem> subjects;
    @SerializedName("wantsInternship")   public Boolean wantsInternship;
    @SerializedName("internshipUrgency") public String  internshipUrgency;
    @SerializedName("wantsScholarships") public Boolean wantsScholarships;
    @SerializedName("wantsExchange")     public Boolean wantsExchange;
    @SerializedName("wantsCompetitions") public Boolean wantsCompetitions;
    @SerializedName("wantsVolunteering") public Boolean wantsVolunteering;
    @SerializedName("wantsStudentOrgs")  public Boolean wantsStudentOrgs;
    @SerializedName("dailyStudyHours")   public String  dailyStudyHours;
    @SerializedName("preferredTime")     public String  preferredTime;
    @SerializedName("reminderDaysAhead") public String  reminderDaysAhead;
    @SerializedName("profileComplete")   public boolean profileComplete;
}
