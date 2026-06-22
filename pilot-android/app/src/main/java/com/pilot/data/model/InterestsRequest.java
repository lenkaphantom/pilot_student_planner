package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InterestsRequest {
    @SerializedName("interests")
    public final List<String> interests;

    public InterestsRequest(List<String> interests) {
        this.interests = interests;
    }
}
