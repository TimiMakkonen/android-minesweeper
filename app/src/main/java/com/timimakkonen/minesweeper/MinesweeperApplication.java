package com.timimakkonen.minesweeper;

import android.app.Application;

import com.timimakkonen.minesweeper.di.ApplicationComponent;
import com.timimakkonen.minesweeper.di.DaggerApplicationComponent;

public class MinesweeperApplication extends Application {

    ApplicationComponent appComponent = DaggerApplicationComponent.create();
}
