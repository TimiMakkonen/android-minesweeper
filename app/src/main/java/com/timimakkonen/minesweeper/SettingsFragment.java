package com.timimakkonen.minesweeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.inject.Inject;

/**
 * <p>
 * This fragment is responsible for displaying and handling all the settings/preferences available
 * for users to modify.
 * </p>
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String USE_NIGHT_MODE_KEY = "use_night_mode";
    private static final String OVERRIDE_SYSTEM_DARK_THEME_KEY = "override_system_dark_theme";
    private static final String DELETE_SAVED_GAME_KEY = "delete_saved_game";
    private static final String PRIM_SECO_SWITCH_HORIZ_BIAS_KEY =
            "prim_seco_switch_horizontal_bias";
    private static final String PRIM_SECO_SWITCH_HORIZ_BIAS_CUSTOM_KEY =
            "prim_seco_switch_horizontal_bias_custom";
    private static final String USE_PRIM_SECO_SWITCH_KEY = "use_prim_seco_switch";

    @Inject
    LocalStorage localStorage;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // set icon for 'use_prim_seco_switch' preference
        Preference primSecoSwitchPref = findPreference(USE_PRIM_SECO_SWITCH_KEY);
        if (primSecoSwitchPref != null) {
            final Drawable primSecoSwitchIcon;
            if (localStorage.getPrimActionIsCheck(true)) {
                primSecoSwitchIcon = ContextCompat.getDrawable(requireActivity(),
                                                               R.drawable.ic_visibility_with_marked_symbol_black_24dp);
            } else {
                primSecoSwitchIcon = ContextCompat.getDrawable(requireActivity(),
                                                               R.drawable.ic_marked_symbol_with_visibility_black_24dp);
            }
            if (primSecoSwitchIcon != null) {
                TypedArray a = requireContext().obtainStyledAttributes(new TypedValue().data,
                                                                       new int[]{android.R.attr.textColorSecondary});
                primSecoSwitchIcon.setColorFilter(a.getColor(0, 0), PorterDuff.Mode.SRC_IN);
                a.recycle();
            }
            primSecoSwitchPref.setIcon(primSecoSwitchIcon);
        }

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
            case PRIM_SECO_SWITCH_HORIZ_BIAS_KEY:
                SeekBarPreference primSecoSwitchCustomHorizBiasPref
                        = findPreference(PRIM_SECO_SWITCH_HORIZ_BIAS_CUSTOM_KEY);
                if (primSecoSwitchCustomHorizBiasPref != null) {
                    if (sharedPreferences.getString(PRIM_SECO_SWITCH_HORIZ_BIAS_KEY, "start")
                                         .equals("custom")) {
                        primSecoSwitchCustomHorizBiasPref.setVisible(true);
                    } else {
                        primSecoSwitchCustomHorizBiasPref.setVisible(false);
                    }
                }
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
                                       localStorage.setHasSavedGame(false);
                                   }
                )
                .show();

    }


}
