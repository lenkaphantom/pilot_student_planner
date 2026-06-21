package com.pilot.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pilot.R;
import com.pilot.data.model.CalendarEventResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter za listu "Predstojeće obaveze" na Home ekranu.
 *
 * Prikazuje max 3 itema (kao na dizajnu). Ostatak je sakriven —
 * korisnik vidi sve na Kalendar ekranu.
 */
public class ObavezaAdapter extends RecyclerView.Adapter<ObavezaViewHolder> {

    private static final int MAX_VISIBLE = 3;

    private List<CalendarEventResponse> items = new ArrayList<>();

    public void setItems(List<CalendarEventResponse> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ObavezaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_obaveza_home, parent, false);
        return new ObavezaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObavezaViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return Math.min(items.size(), MAX_VISIBLE);
    }
}