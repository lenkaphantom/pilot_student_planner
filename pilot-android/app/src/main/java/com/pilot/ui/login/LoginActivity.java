package com.pilot.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.pilot.R;
import com.pilot.data.api.TokenManager;
import com.pilot.databinding.ActivityLoginBinding;
import com.pilot.ui.home.HomeActivity;
import com.pilot.ui.onboarding.OnboardingActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ako je korisnik vec ulogovan, preskoci login
        if (TokenManager.getInstance(this).isLoggedIn()) {
            navigateAfterLogin(TokenManager.getInstance(this).isProfileComplete());
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        // "Prijavi se →"
        binding.btnLogin.setOnClickListener(v -> {
            String email    = getText(binding.etEmail);
            String password = getText(binding.etPassword);
            viewModel.login(email, password);
        });

        // Enter na password polju = submit
        binding.etPassword.setOnEditorActionListener((v, actionId, event) -> {
            binding.btnLogin.performClick();
            return true;
        });


        // "Registruj se"
        binding.tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // "Zaboravljena lozinka?" — TODO: implement
        binding.tvForgotPassword.setOnClickListener(v ->
                showSnackbar("Reset lozinke dolazi uskoro.")
        );
    }


    private void observeViewModel() {
        viewModel.uiState.observe(this, state -> {
            switch (state.status) {
                case LOADING:
                    setLoading(true);
                    clearErrors();
                    break;

                case SUCCESS:
                    setLoading(false);
                    navigateAfterLogin(state.profileComplete);
                    break;

                case VALIDATION_ERROR:
                    setLoading(false);
                    if (state.emailError != null)
                        binding.tilEmail.setError(state.emailError);
                    if (state.passwordError != null)
                        binding.tilPassword.setError(state.passwordError);
                    break;

                case ERROR:
                    setLoading(false);
                    showSnackbar(state.errorMessage);
                    break;

                case IDLE:
                default:
                    setLoading(false);
                    break;
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
        binding.etEmail.setEnabled(!loading);
        binding.etPassword.setEnabled(!loading);
    }

    private void clearErrors() {
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
    }

    private void navigateAfterLogin(boolean profileComplete) {
        Intent intent;
        if (profileComplete) {
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
