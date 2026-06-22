package com.pilot.ui.onboarding;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pilot.data.model.OpportunitiesRequest;
import com.pilot.data.model.SubjectsRequest;
import com.pilot.data.repository.AuthRepository;
import com.pilot.data.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OnboardingViewModel extends AndroidViewModel {

    public static final int TOTAL_STEPS = 5;

    private final ProfileRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Integer> _currentStep = new MutableLiveData<>(1);
    public final LiveData<Integer> currentStep = _currentStep;

    private final MutableLiveData<StepUiState> _uiState = new MutableLiveData<>(StepUiState.idle());
    public final LiveData<StepUiState> uiState = _uiState;

    // ---- Lokalno čuvamo unose između koraka ----

    // Korak 1
    public String faculty      = "";
    public String studyProgram = "";
    public int    yearOfStudy  = 3;

    // Korak 2
    public final List<String> selectedInterests = new ArrayList<>();

    // Korak 3
    public final List<SubjectsRequest.SubjectItem> subjects = new ArrayList<>();

    // Korak 4
    public final OpportunitiesRequest opportunities = new OpportunitiesRequest();

    // Korak 5
    public String dailyStudyHours   = "1-2h";
    public String preferredTime     = "Popodne";
    public int    reminderDaysAhead = 3;

    public OnboardingViewModel(@NonNull Application application) {
        super(application);
        repository = new ProfileRepository(application);
    }

    // ---- Navigacija ----

    public void goToStep(int step) {
        _currentStep.setValue(step);
    }

    public int getCurrentStepValue() {
        Integer v = _currentStep.getValue();
        return v != null ? v : 1;
    }

    public void resetState() {
        _uiState.setValue(StepUiState.idle());
    }

    // ---- Submit po koraku ----

    public void submitStep1(String faculty, String studyProgram, int year) {
        this.faculty       = faculty;
        this.studyProgram  = studyProgram;
        this.yearOfStudy   = year;

        if (faculty.trim().isEmpty()) {
            _uiState.setValue(StepUiState.fieldError("faculty", "Izaberi fakultet."));
            return;
        }

        if (studyProgram.trim().isEmpty()) {
            _uiState.setValue(StepUiState.fieldError("studyProgram", "Unesi smer ili studijski program."));
            return;
        }

        _uiState.setValue(StepUiState.loading());
        executor.execute(() -> {
            AuthRepository.Result<?> r = repository.saveStep1(faculty, studyProgram, year);
            _uiState.postValue(r.isSuccess
                    ? StepUiState.stepSaved(2)
                    : StepUiState.error(r.error));
        });
    }

    public void submitStep2() {
        _uiState.setValue(StepUiState.loading());
        executor.execute(() -> {
            AuthRepository.Result<?> r = repository.saveStep2(new java.util.ArrayList<>(selectedInterests));
            _uiState.postValue(r.isSuccess
                    ? StepUiState.stepSaved(3)
                    : StepUiState.error(r.error));
        });
    }

    public void submitStep3() {
        if (subjects.isEmpty()) {
            _uiState.setValue(StepUiState.fieldError("subjects", "Dodaj bar jedan predmet."));
            return;
        }

        _uiState.setValue(StepUiState.loading());
        executor.execute(() -> {
            AuthRepository.Result<?> r = repository.saveStep3(new java.util.ArrayList<>(subjects));
            _uiState.postValue(r.isSuccess
                    ? StepUiState.stepSaved(4)
                    : StepUiState.error(r.error));
        });
    }

    public void submitStep4() {
        _uiState.setValue(StepUiState.loading());
        executor.execute(() -> {
            AuthRepository.Result<?> r = repository.saveStep4(opportunities);
            _uiState.postValue(r.isSuccess
                    ? StepUiState.stepSaved(5)
                    : StepUiState.error(r.error));
        });
    }

    public void submitStep5() {
        _uiState.setValue(StepUiState.loading());
        executor.execute(() -> {
            AuthRepository.Result<?> r = repository.saveStep5(
                    dailyStudyHours, preferredTime, reminderDaysAhead);
            if (r.isSuccess) {
                repository.markProfileComplete();
                _uiState.postValue(StepUiState.onboardingComplete());
            } else {
                _uiState.postValue(StepUiState.error(r.error));
            }
        });
    }

    /** Preskoči sve — direktno u app. */
    public void skipAll() {
        repository.markProfileComplete();
        _uiState.setValue(StepUiState.onboardingComplete());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }

    // ---- UI State ----

    public static class StepUiState {
        public enum Status { IDLE, LOADING, STEP_SAVED, FIELD_ERROR, ERROR, COMPLETE }

        public final Status status;
        public final int    nextStep;
        public final String errorMessage;
        public final String errorField;

        private StepUiState(Status status, int nextStep, String errorMessage, String errorField) {
            this.status       = status;
            this.nextStep     = nextStep;
            this.errorMessage = errorMessage;
            this.errorField   = errorField;
        }

        public static StepUiState idle()    { return new StepUiState(Status.IDLE,       0, null, null); }
        public static StepUiState loading() { return new StepUiState(Status.LOADING,    0, null, null); }

        public static StepUiState stepSaved(int next) {
            return new StepUiState(Status.STEP_SAVED, next, null, null);
        }
        public static StepUiState onboardingComplete() {
            return new StepUiState(Status.COMPLETE, 0, null, null);
        }
        public static StepUiState error(String msg) {
            return new StepUiState(Status.ERROR, 0, msg, null);
        }
        public static StepUiState fieldError(String field, String msg) {
            return new StepUiState(Status.FIELD_ERROR, 0, msg, field);
        }
    }
}
