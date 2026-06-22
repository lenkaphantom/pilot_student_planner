package com.pilot.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pilot.databinding.FragmentStep3SubjectsBinding;
import com.pilot.data.model.SubjectsRequest;

import java.util.Arrays;
import java.util.List;

public class Step3Fragment extends Fragment {

    private FragmentStep3SubjectsBinding binding;
    private OnboardingViewModel viewModel;
    private SubjectAdapter adapter;

    private static final List<String> GOALS = Arrays.asList(
            "Ocena 10", "Ocena 9+", "Ocena 8", "Ocena 7", "Položiti"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStep3SubjectsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        setupRecyclerView();
        setupGoalDropdown();
        setupClickListeners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new SubjectAdapter(viewModel.subjects, position -> {
            viewModel.subjects.remove(position);
            adapter.notifyItemRemoved(position);
        });
        binding.rvSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSubjects.setAdapter(adapter);
    }

    private void setupGoalDropdown() {
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                GOALS
        );
        binding.etGoal.setAdapter(goalAdapter);
        binding.etGoal.setText(GOALS.get(1), false); // default: Ocena 9+
    }

    private void setupClickListeners() {
        binding.btnAddSubject.setOnClickListener(v -> {
            String name = binding.etSubjectName.getText() != null
                    ? binding.etSubjectName.getText().toString().trim() : "";
            String goal = binding.etGoal.getText() != null
                    ? binding.etGoal.getText().toString().trim() : "Položiti";

            if (name.isEmpty()) {
                binding.tilSubjectName.setError("Unesi naziv predmeta.");
                return;
            }
            binding.tilSubjectName.setError(null);

            viewModel.subjects.add(new SubjectsRequest.SubjectItem(name, goal));
            adapter.notifyItemInserted(viewModel.subjects.size() - 1);

            // Reset forme
            binding.etSubjectName.setText("");
            binding.etGoal.setText(GOALS.get(1), false);
        });

        binding.btnNext.setOnClickListener(v -> viewModel.submitStep3());

        binding.tvSkip.setOnClickListener(v ->
                ((OnboardingActivity) requireActivity()).goToStep(4));
    }

    private void observeViewModel() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            boolean loading = state.status == OnboardingViewModel.StepUiState.Status.LOADING;
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnNext.setEnabled(!loading);
            binding.btnAddSubject.setEnabled(!loading);

            if (state.status == OnboardingViewModel.StepUiState.Status.FIELD_ERROR) {
                if ("subjects".equals(state.errorField)) {
                    // Prikaži error na recycler view kroz snackbar
                    if (getView() != null && state.errorMessage != null) {
                        com.google.android.material.snackbar.Snackbar.make(
                                getView(),
                                state.errorMessage,
                                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                        ).show();
                    }
                    binding.rvSubjects.setAlpha(0.5f); // Vizuelna indikacija greške
                }
            } else if (state.status == OnboardingViewModel.StepUiState.Status.ERROR) {
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
            } else {
                binding.rvSubjects.setAlpha(1.0f);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}