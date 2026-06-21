package com.pilot.ui.home;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pilot.R;
import com.pilot.data.model.CalendarEventResponse;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * ViewHolder za jedan red u listi "Predstojeće obaveze" na Home ekranu.
 * Koristi item_obaveza_home.xml layout.
 */
public class ObavezaViewHolder extends RecyclerView.ViewHolder {

    private final View     ivDot;
    private final TextView tvNaziv;
    private final TextView tvLokacija;
    private final TextView tvRok;

    public ObavezaViewHolder(@NonNull View itemView) {
        super(itemView);
        ivDot      = itemView.findViewById(R.id.iv_dot);
        tvNaziv    = itemView.findViewById(R.id.tv_naziv);
        tvLokacija = itemView.findViewById(R.id.tv_lokacija);
        tvRok      = itemView.findViewById(R.id.tv_rok);
    }

    public void bind(CalendarEventResponse event) {
        Context ctx = itemView.getContext();

        // ── Naziv ─────────────────────────────────────────
        tvNaziv.setText(event.getTitle());

        // ── Lokacija / tip eventa ─────────────────────────
        // Backend nema posebno polje za lokaciju — koristimo subjectName
        // Kad dodaš lokaciju na backendu, zameni ovde
        String sub = event.getSubjectName();
        tvLokacija.setText(sub != null ? sub : formatEventType(event.getEventType()));

        // ── Rok badge + boja tačke ────────────────────────
        long daysUntil = event.getDaysUntil();
        bindUrgency(ctx, daysUntil);
    }

    private void bindUrgency(Context ctx, long daysUntil) {
        if (daysUntil <= 0) {
            // Danas
            setColors(ctx,
                    R.color.pilot_error,
                    R.color.pilot_dark_red,
                    R.drawable.bg_chip_dark_red);
            tvRok.setText(ctx.getString(R.string.rok_danas));

        } else if (daysUntil <= 3) {
            // 1–3 dana — crvena
            setColors(ctx,
                    R.color.pilot_error,
                    R.color.pilot_dark_red,
                    R.drawable.bg_chip_dark_red);
            tvRok.setText(ctx.getString(R.string.rok_za_dana, (int) daysUntil));

        } else if (daysUntil <= 7) {
            // 4–7 dana — narandžasta
            setColors(ctx,
                    R.color.pilot_warning,
                    R.color.pilot_yellow,
                    R.drawable.bg_chip_yellow);
            tvRok.setText(ctx.getString(R.string.rok_za_dana, (int) daysUntil));

        } else {
            // Dalje — siva, prikaži datum
            setColors(ctx,
                    R.color.pilot_gray_border,
                    R.color.pilot_gray_border,
                    R.drawable.bg_chip_white);
            tvRok.setText(formatDate(daysUntil));
        }
    }

    private void setColors(Context ctx, int dotColorRes, int textColorRes, int chipBgRes) {
        ivDot.getBackground().setTint(ContextCompat.getColor(ctx, dotColorRes));
        tvRok.setTextColor(ContextCompat.getColor(ctx, textColorRes));
        tvRok.setBackgroundResource(chipBgRes);
    }

    /** Formira datum od danas + daysUntil dana, npr. "31. maj" */
    private String formatDate(long daysUntil) {
        java.time.LocalDate date = java.time.LocalDate.now().plusDays(daysUntil);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d. MMM", new Locale("sr"));
        return date.format(fmt);
    }

    private String formatEventType(String type) {
        if (type == null) return "";
        switch (type) {
            case "EXAM":       return "Ispit";
            case "COLLOQUIUM": return "Kolokvijum";
            case "DEADLINE":   return "Predaja";
            case "PERSONAL":   return "Lično";
            default:           return type;
        }
    }
}