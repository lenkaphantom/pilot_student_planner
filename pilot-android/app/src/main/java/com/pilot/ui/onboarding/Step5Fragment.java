package com.pilot.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pilot.databinding.FragmentStep5LearningBinding;

public class Step5Fragment extends Fragment {

    private FragmentStep5LearningBinding binding;
    private OnboardingViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStep5LearningBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        restoreState();
        setupClickListeners();
        observeViewModel();
    }

    private void restoreState() {
        // Sati — ID-evi: chipHoursLt1, chipHours1to2, chipHours2to4, chipHoursGt4
        switch (viewModel.dailyStudyHours) {
            case "<1h":  binding.chipHoursLt1.setChecked(true);   break;
            case "2-4h": binding.chipHours2to4.setChecked(true);  break;
            case "4h+":  binding.chipHoursGt4.setChecked(true);   break;
            default:     binding.chipHours1to2.setChecked(true);  break;
        }
        // Vreme — ID-evi: chipTimeMorning, chipTimeForenoon, chipTimeAfternoon, chipTimeEvening
        switch (viewModel.preferredTime) {
            case "Jutro":    binding.chipTimeMorning.setChecked(true);   break;
            case "Prepodne": binding.chipTimeForenoon.setChecked(true);  break;
            case "Uveče":    binding.chipTimeEvening.setChecked(true);   break;
            default:         binding.chipTimeAfternoon.setChecked(true); break;
        }
        // Podsetnik — ID-evi: chipReminder1day, chipReminder3days, chipReminder7days, chipReminder14days
        switch (viewModel.reminderDaysAhead) {
            case 1:  binding.chipReminder1day.setChecked(true);   break;
            case 7:  binding.chipReminder7days.setChecked(true);  break;
            case 14: binding.chipReminder14days.setChecked(true); break;
            default: binding.chipReminder3days.setChecked(true);  break;
        }
    }

    private void setupClickListeners() {
        binding.btnNext.setOnClickListener(v -> {
            viewModel.dailyStudyHours   = getSelectedHours();
            viewModel.preferredTime     = getSelectedTime();
            viewModel.reminderDaysAhead = getSelectedReminder();
            viewModel.submitStep5();
        });

        binding.tvSkip.setOnClickListener(v -> viewModel.skipAll());
    }

    private String getSelectedHours() {
        if (binding.chipHoursLt1.isChecked())  return "<1h";
        if (binding.chipHours2to4.isChecked()) return "2-4h";
        if (binding.chipHoursGt4.isChecked())  return "4h+";
        return "1-2h";
    }

    private String getSelectedTime() {
        if (binding.chipTimeMorning.isChecked())  return "Jutro";
        if (binding.chipTimeForenoon.isChecked()) return "Prepodne";
        if (binding.chipTimeEvening.isChecked())  return "Uveče";
        return "Popodne";
    }

    private int getSelectedReminder() {
        if (binding.chipReminder1day.isChecked())   return 1;
        if (binding.chipReminder7days.isChecked())  return 7;
        if (binding.chipReminder14days.isChecked()) return 14;
        return 3;
    }

    private void observeViewModel() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            boolean loading = state.status == OnboardingViewModel.StepUiState.Status.LOADING;
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnNext.setEnabled(!loading);
            binding.tvSkip.setEnabled(!loading);

            if (state.status == OnboardingViewModel.StepUiState.Status.ERROR) {
                // Prikaži backend grešku
                if (getView() != null && state.errorMessage != null) {
                    com.google.android.material.snackbar.Snackbar.make(
                            getView(),
                            state.errorMessage,
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    ).show();
                }
                // Omogući dugme da korisnik može pokušati ponovo
                binding.btnNext.setEnabled(true);
                binding.tvSkip.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}