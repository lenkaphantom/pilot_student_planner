package com.pilot.ui.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingPagerAdapter extends FragmentStateAdapter {

    public OnboardingPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new Step1Fragment();
            case 1: return new Step2Fragment();
            case 2: return new Step3Fragment();
            case 3: return new Step4Fragment();
            case 4: return new Step5Fragment();
            default: throw new IllegalArgumentException("Nepoznat korak: " + position);
        }
    }

    @Override
    public int getItemCount() { return 5; }
}
