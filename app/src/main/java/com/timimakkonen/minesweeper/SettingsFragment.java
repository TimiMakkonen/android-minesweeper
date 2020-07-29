package com.timimakkonen.minesweeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsFragment";

    private static final String USE_NIGHT_MODE_KEY = "use_night_mode";
    private static final String OVERRIDE_SYSTEM_DARK_THEME_KEY = "override_system_dark_theme";
    private static final String DELETE_SAVED_GAME_KEY = "delete_saved_game";

    private static final String HAS_SAVED_GAME_KEY = "has_saved_game";

    @Inject
    LocalStorage localStorage;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        setOnPreferenceClickListeners();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        ((MinesweeperApplication) requireActivity().getApplicationContext())
                .appComponent
                .inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case USE_NIGHT_MODE_KEY:
                if (sharedPreferences.getBoolean(USE_NIGHT_MODE_KEY, false)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;
            case OVERRIDE_SYSTEM_DARK_THEME_KEY:
                if (sharedPreferences.getBoolean(OVERRIDE_SYSTEM_DARK_THEME_KEY, false)) {
                    if (sharedPreferences.getBoolean(USE_NIGHT_MODE_KEY, false)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                } else {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                break;
        }
    }

    private void setOnPreferenceClickListeners() {

        Preference deleteSavedGame = findPreference(DELETE_SAVED_GAME_KEY);
        if (deleteSavedGame != null) {
            deleteSavedGame.setOnPreferenceClickListener(
                    preference -> {
                        showDeleteSavedGameDialog();
                        return true;
                    });
        }
    }

    private void showDeleteSavedGameDialog() {

        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.delete_saved_game_dialog_title)
                .setMessage(R.string.delete_saved_game_dialog_message)
                .setNeutralButton(R.string.cancel, null)
                .setPositiveButton(R.string.accept,
                                   (dialog, which) -> {
                                       localStorage.deleteCurrentMinesweeperGame();
                                       localStorage.setBoolean(HAS_SAVED_GAME_KEY, false);
                                   }
                )
                .show();

    }


}
