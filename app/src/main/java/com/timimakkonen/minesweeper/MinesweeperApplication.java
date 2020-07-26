package com.timimakkonen.minesweeper;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.timimakkonen.minesweeper.di.ApplicationComponent;
import com.timimakkonen.minesweeper.di.DaggerApplicationComponent;

public class MinesweeperApplication extends Application {

    private static final String USE_NIGHT_MODE_KEY = "use_night_mode";
    private static final String OVERRIDE_SYSTEM_DARK_THEME_KEY = "override_system_dark_theme";

    final ApplicationComponent appComponent = DaggerApplicationComponent.create();

    @Override
    public void onCreate() {
        super.onCreate();

        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(OVERRIDE_SYSTEM_DARK_THEME_KEY, false)) {
            if (sharedPreferences.getBoolean(USE_NIGHT_MODE_KEY, false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}
