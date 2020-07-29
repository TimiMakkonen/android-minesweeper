package com.timimakkonen.minesweeper.di;

import android.content.Context;

import com.timimakkonen.minesweeper.MinesweeperApplication;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ApplicationModule {

    @Binds
    public abstract Context bindApplication(MinesweeperApplication minesweeperApplication);

}
