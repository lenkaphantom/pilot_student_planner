package com.pilot.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.pilot.databinding.ActivityOnboardingBinding;
import com.pilot.ui.home.HomeActivity;
import com.pilot.ui.login.RegisterActivity;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private OnboardingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);

        binding.viewPager.setAdapter(new OnboardingPagerAdapter(this));
        binding.viewPager.setUserInputEnabled(false);

        setupBackButton();
        observeViewModel();
    }

    /** Pozivaju fragmenti kada žele da pređu na sledeći korak. */
    public void goToStep(int step) {
        int page = step - 1; // ViewPager2 je 0-based
        if (page < 0) page = 0;
        if (page > 4) page = 4;
        binding.viewPager.setCurrentItem(page, true);
        updateHeader(step);
        viewModel.goToStep(step);
        viewModel.resetState();
    }

    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem(); // 0-based
            if (current == 0) {
                // Ako smo na koraku 1, idi nazad na RegisterActivity
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                goToStep(current); // current je 0-based → goToStep(current) = prethodni korak
            }
        });
    }

    private void updateHeader(int step) {
        binding.tvStepLabel.setText("Korak " + step + "/5");
        binding.progressBar.setProgress(step);
    }

    private void observeViewModel() {
        viewModel.currentStep.observe(this, step -> updateHeader(step));

        viewModel.uiState.observe(this, state -> {
            switch (state.status) {
                case STEP_SAVED:
                    goToStep(state.nextStep);
                    break;
                case COMPLETE:
                    navigateToHome();
                    break;
                case ERROR:
                    Snackbar.make(
                            binding.getRoot(),
                            state.errorMessage != null ? state.errorMessage : "Greška",
                            Snackbar.LENGTH_LONG
                    ).show();
                    break;
                default:
                    break;
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        int current = binding.viewPager.getCurrentItem();
        if (current > 0) {
            goToStep(current); // ide na prethodni korak
        } else {
            // Ako smo na koraku 1, idi nazad na RegisterActivity
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
