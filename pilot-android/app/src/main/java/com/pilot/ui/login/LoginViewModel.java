package com.pilot.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pilot.data.model.AuthResponse;
import com.pilot.data.repository.AuthRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<LoginUiState> _uiState = new MutableLiveData<>(LoginUiState.idle());
    public final LiveData<LoginUiState> uiState = _uiState;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
    }

    public void login(String email, String password) {
        // Lokalna validacija pre poziva na mrežu
        String emailError = validateEmail(email);
        String passwordError = validatePassword(password);

        if (emailError != null || passwordError != null) {
            _uiState.setValue(LoginUiState.validationError(emailError, passwordError));
            return;
        }

        _uiState.setValue(LoginUiState.loading());

        executor.execute(() -> {
            AuthRepository.Result<AuthResponse> result = repository.login(email, password);
            if (result.isSuccess) {
                boolean profileComplete = result.data.user != null && result.data.user.profileComplete;
                _uiState.postValue(LoginUiState.success(profileComplete));
            } else {
                _uiState.postValue(LoginUiState.error(result.error));
            }
        });
    }

    // ---- Validacija ----

    private String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) return "Email je obavezan.";
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
            return "Email nije u ispravnom formatu.";
        return null;
    }

    private String validatePassword(String password) {
        if (password == null || password.isEmpty()) return "Lozinka je obavezna.";
        if (password.length() < 8) return "Lozinka mora imati najmanje 8 karaktera.";
        return null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }

    // ---- UI State sealed-class replacement ----

    public static class LoginUiState {
        public enum Status { IDLE, LOADING, SUCCESS, VALIDATION_ERROR, ERROR }

        public final Status status;
        public final boolean profileComplete;  // za rutiranje posle logina
        public final String errorMessage;
        public final String emailError;
        public final String passwordError;

        private LoginUiState(
                Status status, boolean profileComplete,
                String errorMessage, String emailError, String passwordError
        ) {
            this.status = status;
            this.profileComplete = profileComplete;
            this.errorMessage = errorMessage;
            this.emailError = emailError;
            this.passwordError = passwordError;
        }

        public static LoginUiState idle() {
            return new LoginUiState(Status.IDLE, false, null, null, null);
        }

        public static LoginUiState loading() {
            return new LoginUiState(Status.LOADING, false, null, null, null);
        }

        public static LoginUiState success(boolean profileComplete) {
            return new LoginUiState(Status.SUCCESS, profileComplete, null, null, null);
        }

        public static LoginUiState validationError(String emailError, String passwordError) {
            return new LoginUiState(Status.VALIDATION_ERROR, false, null, emailError, passwordError);
        }

        public static LoginUiState error(String message) {
            return new LoginUiState(Status.ERROR, false, message, null, null);
        }
    }
}
