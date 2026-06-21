package com.pilot.data.api;

import com.pilot.data.model.CalendarEventResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit interfejs za Home ekran.
 * AuthInterceptor automatski dodaje Bearer token u header.
 */
public interface HomeApiService {

    /**
     * GET /api/calendar/events/upcoming
     * Vraća evente u narednih 7 dana — koristi se za
     * sekciju "Predstojeće obaveze" na Home ekranu.
     */
    @GET("calendar/events/upcoming")
    Call<List<CalendarEventResponse>> getUpcomingEvents();
}