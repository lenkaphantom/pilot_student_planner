package com.pilot.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.pilot.R;
import com.pilot.databinding.ActivityRegisterBinding;
import com.pilot.ui.home.HomeActivity;
import com.pilot.ui.onboarding.OnboardingActivity;

/**
 * Registracija ekran — "Kreiraj nalog".
 *
 * Flow:
 *  - unos: ime, email, lozinka, potvrda lozinke
 *  - "Nastavi ka podešavanju →" → backend register → OnboardingActivity
 *  - "Preskoči podešavanja" → backend register → Main (TODO)
 *  - "Prijavi se" → nazad na LoginActivity
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    private boolean skipSetup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        setupHeader();
        setupClickListeners();
        observeViewModel();
    }

    private void setupHeader() {
        binding.header.setTitle(R.string.title_register);
        binding.header.setOnBackClickListener(v -> finish());
    }

    private void setupClickListeners() {

        // "Nastavi ka podešavanju →"
        binding.btnRegister.setOnClickListener(v -> {
            skipSetup = false;
            submitForm();
        });

        // "Preskoči podešavanja, uđi direktno →"
        binding.tvSkipSetup.setOnClickListener(v -> {
            skipSetup = true;
            submitForm();
        });

        // "Prijavi se"
        binding.tvLogin.setOnClickListener(v -> finish());

        // Enter na poslednjem polju = submit
        binding.etConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            binding.btnRegister.performClick();
            return true;
        });
    }

    private void submitForm() {
        clearErrors();
        viewModel.register(
                getText(binding.etFullName),
                getText(binding.etEmail),
                getText(binding.etPassword),
                getText(binding.etConfirmPassword)
        );
    }

    private void observeViewModel() {
        viewModel.uiState.observe(this, state -> {
            switch (state.status) {
                case LOADING:
                    setLoading(true);
                    break;

                case SUCCESS:
                    setLoading(false);
                    navigateAfterRegister();
                    break;

                case VALIDATION_ERROR:
                    setLoading(false);
                    RegisterViewModel.ValidationErrors e = state.validationErrors;
                    if (e.fullNameError != null)
                        binding.tilFullName.setError(e.fullNameError);
                    if (e.emailError != null)
                        binding.tilEmail.setError(e.emailError);
                    if (e.passwordError != null)
                        binding.tilPassword.setError(e.passwordError);
                    if (e.confirmPasswordError != null)
                        binding.tilConfirmPassword.setError(e.confirmPasswordError);
                    break;

                case ERROR:
                    setLoading(false);
                    showSnackbar(state.errorMessage != null ? state.errorMessage : "Greška pri registraciji.");
                    break;

                case IDLE:
                default:
                    break;
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!loading);
        binding.tvSkipSetup.setEnabled(!loading);
        binding.etFullName.setEnabled(!loading);
        binding.etEmail.setEnabled(!loading);
        binding.etPassword.setEnabled(!loading);
        binding.etConfirmPassword.setEnabled(!loading);
    }

    private void clearErrors() {
        binding.tilFullName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);
    }

    private void navigateAfterRegister() {
        Intent intent;
        if (skipSetup) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, OnboardingActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private String getText(com.google.android.material.textfield.TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
