package com.pilot.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.pilot.R;
import com.pilot.databinding.FragmentStep1ProfileBinding;

public class Step1Fragment extends Fragment {

    private FragmentStep1ProfileBinding binding;
    private OnboardingViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStep1ProfileBinding.inflate(inflater, container, false);
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
        // Vraćamo prethodno unesene vrednosti ako korisnik navigira nazad
        if (!viewModel.faculty.isEmpty())
            binding.etFaculty.setText(viewModel.faculty);
        if (!viewModel.studyProgram.isEmpty())
            binding.etStudyProgram.setText(viewModel.studyProgram);

        // Čip za godinu
        int year = viewModel.yearOfStudy;
        int[] chipIds = {R.id.chip1, R.id.chip2, R.id.chip3, R.id.chip4,
                R.id.chip5, R.id.chipM, R.id.chipD};
        if (year >= 1 && year <= chipIds.length) {
            Chip chip = binding.getRoot().findViewById(chipIds[year - 1]);
            if (chip != null) chip.setChecked(true);
        }
    }

    private void setupClickListeners() {
        binding.btnNext.setOnClickListener(v -> {
            String faculty = binding.etFaculty.getText() != null
                    ? binding.etFaculty.getText().toString().trim() : "";
            String studyProgram = binding.etStudyProgram.getText() != null
                    ? binding.etStudyProgram.getText().toString().trim() : "";
            int year = getSelectedYear();
            viewModel.submitStep1(faculty, studyProgram, year);
        });

        binding.tvSkip.setOnClickListener(v ->
                ((OnboardingActivity) requireActivity()).goToStep(2));
    }

    private int getSelectedYear() {
        int[] chipIds = {R.id.chip1, R.id.chip2, R.id.chip3, R.id.chip4,
                R.id.chip5, R.id.chipM, R.id.chipD};
        for (int i = 0; i < chipIds.length; i++) {
            Chip chip = binding.getRoot().findViewById(chipIds[i]);
            if (chip != null && chip.isChecked()) return i + 1;
        }
        return 1;
    }

    private void observeViewModel() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            boolean loading = state.status == OnboardingViewModel.StepUiState.Status.LOADING;
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnNext.setEnabled(!loading);

            if (state.status == OnboardingViewModel.StepUiState.Status.FIELD_ERROR) {
                // Validacijska greška - prikaži na poljima
                if ("faculty".equals(state.errorField))
                    binding.tilFaculty.setError(state.errorMessage);
                if ("studyProgram".equals(state.errorField))
                    binding.tilStudyProgram.setError(state.errorMessage);
            } else if (state.status == OnboardingViewModel.StepUiState.Status.ERROR) {
                // Serverska greška - prikaži snackbar (ako je dostupan)
                if (getView() != null && state.errorMessage != null) {
                    Snackbar.make(
                            getView(),
                            state.errorMessage,
                            Snackbar.LENGTH_LONG
                    ).show();
                }
                // Omogući dugme da korisnik može pokušati ponovo
                binding.btnNext.setEnabled(true);
            } else {
                // Očisti greške ako nema ERROR statusa
                binding.tilFaculty.setError(null);
                binding.tilStudyProgram.setError(null);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}