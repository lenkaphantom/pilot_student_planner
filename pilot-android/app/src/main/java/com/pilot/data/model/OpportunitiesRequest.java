package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;

public class OpportunitiesRequest {
    @SerializedName("wantsInternship")   public Boolean wantsInternship   = false;
    @SerializedName("internshipUrgency") public String  internshipUrgency;
    @SerializedName("wantsScholarships") public Boolean wantsScholarships = false;
    @SerializedName("wantsExchange")     public Boolean wantsExchange     = false;
    @SerializedName("wantsCompetitions") public Boolean wantsCompetitions = false;
    @SerializedName("wantsVolunteering") public Boolean wantsVolunteering = false;
    @SerializedName("wantsStudentOrgs")  public Boolean wantsStudentOrgs  = false;
}
