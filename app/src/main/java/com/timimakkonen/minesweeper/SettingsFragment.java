package com.timimakkonen.minesweeper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);


        // TODO: Move and fix these for proper usage
        findPreference("use_night_mode").setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    if (newValue.toString().equals("true")) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                });

        findPreference("override_system_dark_theme").setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    if (newValue.toString().equals("false")) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }
                    return true;
                });
    }

}
