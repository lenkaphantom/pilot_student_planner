package com.pilot.ui.subjects;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pilot.R;
import com.pilot.ui.profile.ProfileActivity;

public class SubjectsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        // da profil ikonica bude selektovana i ljubicasta na dnu ekrana
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profil);

        // klik na sacuvaj izmene isto zatvara ekran i vraca nas nazad
        findViewById(R.id.btn_save).setOnClickListener(v -> {
            finish();
        });
    }
}
