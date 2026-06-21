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

public class RegisterViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<RegisterUiState> _uiState = new MutableLiveData<>(RegisterUiState.idle());
    public final LiveData<RegisterUiState> uiState = _uiState;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
    }

    public void register(String fullName, String email, String password, String confirmPassword) {
        ValidationErrors errors = validate(fullName, email, password, confirmPassword);
        if (errors.hasErrors()) {
            _uiState.setValue(RegisterUiState.validationError(errors));
            return;
        }

        _uiState.setValue(RegisterUiState.loading());

        executor.execute(() -> {
            AuthRepository.Result<AuthResponse> result =
                    repository.register(fullName.trim(), email.trim(), password, confirmPassword);
            if (result.isSuccess) {
                _uiState.postValue(RegisterUiState.success());
            } else {
                _uiState.postValue(RegisterUiState.error(result.error));
            }
        });
    }

    // ---- Validacija ----

    private ValidationErrors validate(
            String fullName, String email, String password, String confirmPassword
    ) {
        ValidationErrors e = new ValidationErrors();
        if (fullName == null || fullName.trim().isEmpty())
            e.fullNameError = "Ime i prezime su obavezni.";
        if (email == null || email.trim().isEmpty())
            e.emailError = "Email je obavezan.";
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
            e.emailError = "Email nije u ispravnom formatu.";
        if (password == null || password.isEmpty())
            e.passwordError = "Lozinka je obavezna.";
        else if (password.length() < 8)
            e.passwordError = "Lozinka mora imati najmanje 8 karaktera.";
        if (confirmPassword == null || !confirmPassword.equals(password))
            e.confirmPasswordError = "Lozinke se ne poklapaju.";
        return e;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }

    // ---- State klase ----

    public static class ValidationErrors {
        public String fullNameError;
        public String emailError;
        public String passwordError;
        public String confirmPasswordError;

        public boolean hasErrors() {
            return fullNameError != null || emailError != null
                    || passwordError != null || confirmPasswordError != null;
        }
    }

    public static class RegisterUiState {
        public enum Status { IDLE, LOADING, SUCCESS, VALIDATION_ERROR, ERROR }

        public final Status status;
        public final String errorMessage;
        public final ValidationErrors validationErrors;

        private RegisterUiState(Status status, String errorMessage, ValidationErrors validationErrors) {
            this.status = status;
            this.errorMessage = errorMessage;
            this.validationErrors = validationErrors;
        }

        public static RegisterUiState idle() {
            return new RegisterUiState(Status.IDLE, null, null);
        }

        public static RegisterUiState loading() {
            return new RegisterUiState(Status.LOADING, null, null);
        }

        public static RegisterUiState success() {
            return new RegisterUiState(Status.SUCCESS, null, null);
        }

        public static RegisterUiState validationError(ValidationErrors errors) {
            return new RegisterUiState(Status.VALIDATION_ERROR, null, errors);
        }

        public static RegisterUiState error(String message) {
            return new RegisterUiState(Status.ERROR, message, null);
        }
    }
}
