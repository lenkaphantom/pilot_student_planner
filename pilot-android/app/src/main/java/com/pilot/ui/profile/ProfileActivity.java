package com.pilot.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.pilot.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.pilot.ui.home.HomeActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // cim se otvori navigacija profil je ljubicast
        bottomNav.setSelectedItemId(R.id.nav_profil);

        // slusamo klikove na donjem meniju dok smo na profilu
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // ako klikne na profil, a vec smo tu, ne radimo nista
                if (id == R.id.nav_profil) {
                    return true;
                }
                // ako klikne na pocetnu (bilo koje dugme koje vodi na HomeActivity)
                else if (id == R.id.nav_home || id == R.id.nav_chat) {
                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });

        // Moji predmeti i ciljevi
        findViewById(R.id.row_predmeti).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, com.pilot.ui.subjects.SubjectsActivity.class);
            startActivity(intent);
        });

        // Interesovanja
        findViewById(R.id.row_interesovanja).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, com.pilot.ui.interests.InterestsActivity.class);
            startActivity(intent);
        });

        // Pracene prilike
        findViewById(R.id.row_pracene_prilike).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, com.pilot.ui.opportunities.FollowedOpportunitiesActivity.class);
            startActivity(intent);
        });

        // Licni podaci
        findViewById(R.id.row_licni_podaci).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, com.pilot.ui.profile_info.PersonalInfoActivity.class);
            startActivity(intent);
        });
    }
}