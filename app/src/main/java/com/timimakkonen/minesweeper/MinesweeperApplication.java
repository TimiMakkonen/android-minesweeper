package com.timimakkonen.minesweeper;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.timimakkonen.minesweeper.di.ApplicationComponent;
import com.timimakkonen.minesweeper.di.DaggerApplicationComponent;

import javax.inject.Inject;

/**
 * This class is the base class for this whole application.
 */
public class MinesweeperApplication extends Application {

    final ApplicationComponent appComponent = DaggerApplicationComponent.factory().create(this);

    @Inject
    LocalStorage localStorage;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent.inject(this);

        if (localStorage.getOverrideSystemDarkTheme(false)) {
            if (localStorage.getUseNightMode(false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}
