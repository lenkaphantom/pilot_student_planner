package com.pilot.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ApiError {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("fieldErrors")
    public Map<String, String> fieldErrors;

    /** Vraca prvu grešku za polje, ili generalnu poruku. */
    public String getFieldError(String field) {
        if (fieldErrors != null && fieldErrors.containsKey(field)) {
            return fieldErrors.get(field);
        }
        return message;
    }
}
