package com.pilot.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pilot.databinding.FragmentStep4OpportunitiesBinding;
import com.pilot.databinding.ItemToggleRowBinding;

public class Step4Fragment extends Fragment {

    private FragmentStep4OpportunitiesBinding binding;
    private OnboardingViewModel viewModel;

    // Binding objekti za svaki include red — ViewBinding ih generiše direktno
    private ItemToggleRowBinding rowInternship;
    private ItemToggleRowBinding rowScholarships;
    private ItemToggleRowBinding rowExchange;
    private ItemToggleRowBinding rowCompetitions;
    private ItemToggleRowBinding rowVolunteering;
    private ItemToggleRowBinding rowStudentOrgs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStep4OpportunitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        // Izvlacimo binding za svaki included layout
        rowInternship   = ItemToggleRowBinding.bind(binding.rowInternship.getRoot());
        rowScholarships = ItemToggleRowBinding.bind(binding.rowScholarships.getRoot());
        rowExchange     = ItemToggleRowBinding.bind(binding.rowExchange.getRoot());
        rowCompetitions = ItemToggleRowBinding.bind(binding.rowCompetitions.getRoot());
        rowVolunteering = ItemToggleRowBinding.bind(binding.rowVolunteering.getRoot());
        rowStudentOrgs  = ItemToggleRowBinding.bind(binding.rowStudentOrgs.getRoot());

        setupToggleRows();
        restoreState();
        setupClickListeners();
        observeViewModel();
    }

    private void setupToggleRows() {
        rowInternship.tvToggleLabel.setText("Praksa u IT kompaniji");
        rowScholarships.tvToggleLabel.setText("Stipendije");
        rowExchange.tvToggleLabel.setText("Razmene (Erasmus...)");
        rowCompetitions.tvToggleLabel.setText("Takmičenja");
        rowVolunteering.tvToggleLabel.setText("Volonterstvo");
        rowStudentOrgs.tvToggleLabel.setText("Studentske organizacije");

        // Kad se uključi internship — prikaži pitanje o hitnosti
        rowInternship.switchToggle.setOnCheckedChangeListener((btn, checked) -> {
            viewModel.opportunities.wantsInternship = checked;
            binding.layoutInternshipUrgency.setVisibility(checked ? View.VISIBLE : View.GONE);
        });

        rowScholarships.switchToggle.setOnCheckedChangeListener((b, c) ->
                viewModel.opportunities.wantsScholarships = c);
        rowExchange.switchToggle.setOnCheckedChangeListener((b, c) ->
                viewModel.opportunities.wantsExchange = c);
        rowCompetitions.switchToggle.setOnCheckedChangeListener((b, c) ->
                viewModel.opportunities.wantsCompetitions = c);
        rowVolunteering.switchToggle.setOnCheckedChangeListener((b, c) ->
                viewModel.opportunities.wantsVolunteering = c);
        rowStudentOrgs.switchToggle.setOnCheckedChangeListener((b, c) ->
                viewModel.opportunities.wantsStudentOrgs = c);
    }

    private void restoreState() {
        rowInternship.switchToggle.setChecked(
                Boolean.TRUE.equals(viewModel.opportunities.wantsInternship));
        rowScholarships.switchToggle.setChecked(
                Boolean.TRUE.equals(viewModel.opportunities.wantsScholarships));
        rowExchange.switchToggle.setChecked(
                Boolean.TRUE.equals(viewModel.opportunities.wantsExchange));
        rowCompetitions.switchToggle.setChecked(
                Boolean.TRUE.equals(viewModel.opportunities.wantsCompetitions));
        rowVolunteering.switchToggle.setChecked(
                Boolean.TRUE.equals(viewModel.opportunities.wantsVolunteering));
        rowStudentOrgs.switchToggle.setChecked(
                Boolean.TRUE.equals(viewModel.opportunities.wantsStudentOrgs));

        if (Boolean.TRUE.equals(viewModel.opportunities.wantsInternship)) {
            binding.layoutInternshipUrgency.setVisibility(View.VISIBLE);
        }

        String urgency = viewModel.opportunities.internshipUrgency;
        if ("MAYBE".equals(urgency))  binding.chipUrgencyMaybe.setChecked(true);
        else if ("NOT_YET".equals(urgency)) binding.chipUrgencyNo.setChecked(true);
        else binding.chipUrgencyYes.setChecked(true);
    }

    private void setupClickListeners() {
        binding.btnNext.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.opportunities.wantsInternship)) {
                if (binding.chipUrgencyMaybe.isChecked())
                    viewModel.opportunities.internshipUrgency = "MAYBE";
                else if (binding.chipUrgencyNo.isChecked())
                    viewModel.opportunities.internshipUrgency = "NOT_YET";
                else
                    viewModel.opportunities.internshipUrgency = "ACTIVE";
            }
            viewModel.submitStep4();
        });

        binding.tvSkip.setOnClickListener(v ->
                ((OnboardingActivity) requireActivity()).goToStep(5));
    }

    private void observeViewModel() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            boolean loading = state.status == OnboardingViewModel.StepUiState.Status.LOADING;
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnNext.setEnabled(!loading);

            if (state.status == OnboardingViewModel.StepUiState.Status.ERROR) {
                // Prikaži backend grešku i omogući dugme da korisnik može pokušati ponovo
                if (getView() != null && state.errorMessage != null) {
                    com.google.android.material.snackbar.Snackbar.make(
                            getView(),
                            state.errorMessage,
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    ).show();
                }
                // Važno: omogući dugme nakon greške
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