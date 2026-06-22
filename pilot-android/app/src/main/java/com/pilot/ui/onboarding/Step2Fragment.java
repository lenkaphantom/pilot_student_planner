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
import com.pilot.R;
import com.pilot.databinding.FragmentStep2InterestsBinding;

import java.util.LinkedHashMap;
import java.util.Map;

public class Step2Fragment extends Fragment {

    private FragmentStep2InterestsBinding binding;
    private OnboardingViewModel viewModel;

    // Mapiranje chip ID -> vrednost koja ide na backend
    private static final Map<Integer, String> CHIP_MAP = new LinkedHashMap<>();
    static {
        CHIP_MAP.put(R.id.chipWebDev,    "Web razvoj");
        CHIP_MAP.put(R.id.chipAiMl,      "AI / ML");
        CHIP_MAP.put(R.id.chipMobile,    "Mobilne app");
        CHIP_MAP.put(R.id.chipData,      "Data science");
        CHIP_MAP.put(R.id.chipUiUx,      "UI/UX");
        CHIP_MAP.put(R.id.chipDevOps,    "DevOps");
        CHIP_MAP.put(R.id.chipGameDev,   "GameDev");
        CHIP_MAP.put(R.id.chipSecurity,  "Cybersecurity");
        CHIP_MAP.put(R.id.chipEmbedded,  "Embedded");
        CHIP_MAP.put(R.id.chipBlockchain,"Blockchain");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStep2InterestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        restoreSelection();
        setupClickListeners();
        observeViewModel();
    }

    private void restoreSelection() {
        for (Map.Entry<Integer, String> entry : CHIP_MAP.entrySet()) {
            Chip chip = binding.getRoot().findViewById(entry.getKey());
            if (chip != null) {
                chip.setChecked(viewModel.selectedInterests.contains(entry.getValue()));
            }
        }
    }

    private void setupClickListeners() {
        binding.btnNext.setOnClickListener(v -> {
            // Prikupljamo selektovane čipove i čuvamo u VM
            viewModel.selectedInterests.clear();
            for (Map.Entry<Integer, String> entry : CHIP_MAP.entrySet()) {
                Chip chip = binding.getRoot().findViewById(entry.getKey());
                if (chip != null && chip.isChecked()) {
                    viewModel.selectedInterests.add(entry.getValue());
                }
            }
            viewModel.submitStep2();
        });

        binding.tvSkip.setOnClickListener(v ->
                ((OnboardingActivity) requireActivity()).goToStep(3));
    }

    private void observeViewModel() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            boolean loading = state.status == OnboardingViewModel.StepUiState.Status.LOADING;
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnNext.setEnabled(!loading);

            if (state.status == OnboardingViewModel.StepUiState.Status.ERROR) {
                // Prikaži backend grešku kroz snackbar
                if (getView() != null && state.errorMessage != null) {
                    com.google.android.material.snackbar.Snackbar.make(
                            getView(),
                            state.errorMessage,
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    ).show();
                }
                // Omogući dugme da korisnik može pokušati ponovo
                binding.btnNext.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}