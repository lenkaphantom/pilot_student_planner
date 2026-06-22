package com.pilot.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pilot.R;
import com.pilot.data.api.HomeApiService;
import com.pilot.data.api.RetrofitClient;
import com.pilot.data.api.TokenManager;
import com.pilot.data.model.CalendarEventResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    // ── Views ─────────────────────────────────────────
    private TextView            tvGreeting;
    private TextView            tvDate;
    private TextView            tvNastaviSubtitle;
    private ProgressBar         progressNastavi;
    private RecyclerView        rvObaveze;
    private TextView            layoutEmpty;        // prikazuje se kad nema obaveza
    private BottomNavigationView bottomNav;

    // ── Data ──────────────────────────────────────────
    private ObavezaAdapter      adapter;
    private HomeApiService      apiService;
    private TokenManager        tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tokenManager = TokenManager.getInstance(this);
        apiService   = RetrofitClient.getInstance(this)
                .create(HomeApiService.class);

        bindViews();
        setupRecyclerView();
        setupBottomNav();
        populateStaticContent();
        loadUpcomingEvents();
    }

    // ── Bind ──────────────────────────────────────────

    private void bindViews() {
        tvGreeting        = findViewById(R.id.tv_greeting);
        tvDate            = findViewById(R.id.tv_date);
        tvNastaviSubtitle = findViewById(R.id.tv_nastavi_subtitle);
        progressNastavi   = findViewById(R.id.progress_nastavi);
        rvObaveze         = findViewById(R.id.rv_obaveze);
        layoutEmpty       = findViewById(R.id.layout_empty_obaveze);
        bottomNav         = findViewById(R.id.bottom_nav);

        // "Nastavi učenje" dugme — za sad nema destinacije
        findViewById(R.id.btn_nastavi_ucenje).setOnClickListener(v ->
                Toast.makeText(this, "Plan učenja — uskoro", Toast.LENGTH_SHORT).show()
        );

        // Prilika 1 — Stipendija
        View prilika1 = findViewById(R.id.item_prilika_1);
        ((TextView) prilika1.findViewById(R.id.tv_tip_badge)).setText("STIPENDIJA");
        ((TextView) prilika1.findViewById(R.id.tv_naziv)).setText("Fond za mlade talente Srbije");
        ((TextView) prilika1.findViewById(R.id.tv_rok)).setText("Rok: 1. jun");
        prilika1.findViewById(R.id.tv_tip_badge).setBackgroundResource(R.drawable.bg_chip_green);
        ((TextView) prilika1.findViewById(R.id.tv_tip_badge)).setTextColor(getColor(R.color.pilot_green));

        // Prilika 2 — Praksa
        View prilika2 = findViewById(R.id.item_prilika_2);
        ((TextView) prilika2.findViewById(R.id.tv_tip_badge)).setText("PRAKSA");
        ((TextView) prilika2.findViewById(R.id.tv_naziv)).setText("Nordeus - letnji developer program");
        ((TextView) prilika2.findViewById(R.id.tv_rok)).setText("Rok: 15. jun");
        prilika2.findViewById(R.id.tv_tip_badge).setBackgroundResource(R.drawable.bg_chip_purple);
        ((TextView) prilika2.findViewById(R.id.tv_tip_badge)).setTextColor(getColor(R.color.pilot_tirkiz));
    }

    // ── RecyclerView ──────────────────────────────────

    private void setupRecyclerView() {
        adapter = new ObavezaAdapter();
        rvObaveze.setLayoutManager(new LinearLayoutManager(this));
        rvObaveze.setAdapter(adapter);
        rvObaveze.setNestedScrollingEnabled(false);
    }

    // ── Bottom navigation ─────────────────────────────

    private void setupBottomNav() {
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true; // već smo ovde
            }

            // povezivanje na profil
            else if (id == R.id.nav_profil) {
                android.content.Intent intent = new android.content.Intent(HomeActivity.this, com.pilot.ui.profile.ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            Toast.makeText(this, "Uskoro...", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    // ── Statički sadržaj ──────────────────────────────

    private void populateStaticContent() {
        // Pozdrav — ime iz SharedPreferences (sačuvano pri loginu)
        String fullName = tokenManager.getUserFullName();
        String firstName = fullName != null ? fullName.trim().split("\\s+")[0] : "";
        tvGreeting.setText(getGreeting(firstName));

        // Datum
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy.", new Locale("sr", "Latn"));
        String dateStr = today.format(fmt);
        // Kapitalizuj prvi karakter
        tvDate.setText(capitalize(dateStr));

        // "Nastavi gde si stao" — hardkodovano dok ne bude API
        tvNastaviSubtitle.setText("Baze Podataka - Poglavlje 4: Normalizacija");
        progressNastavi.setProgress(55);
    }

    /**
     * Vraća odgovarajući pozdrav prema dobu dana.
     * "Dobro jutro" (5–11), "Dobar dan" (12–17), "Dobro veče" (18–4)
     */
    private String getGreeting(String firstName) {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String prefix;
        if (hour >= 5 && hour < 12) {
            prefix = getString(R.string.greeting_morning, firstName);
        } else if (hour < 18) {
            prefix = getString(R.string.greeting_day, firstName);
        } else {
            prefix = getString(R.string.greeting_evening, firstName);
        }
        return prefix;
    }

    // ── API poziv ─────────────────────────────────────

    private void loadUpcomingEvents() {
        apiService.getUpcomingEvents().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<CalendarEventResponse>> call,
                                   Response<List<CalendarEventResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CalendarEventResponse> events = response.body();
                    android.util.Log.d("HOME", "Broj eventa: " + events.size());
                    for (CalendarEventResponse e : events) {
                        android.util.Log.d("HOME", "Event: " + e.getTitle() + " | " + e.getStartTime());
                    }

                    adapter.setItems(events);

                    boolean isEmpty = events.isEmpty();
                    layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                    rvObaveze.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                } else {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvObaveze.setVisibility(View.GONE);
                    showError("Greška pri učitavanju obaveza (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<CalendarEventResponse>> call, Throwable t) {
                showError("Nema internet konekcije");
            }
        });
    }

    // ── Helpers ───────────────────────────────────────

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}