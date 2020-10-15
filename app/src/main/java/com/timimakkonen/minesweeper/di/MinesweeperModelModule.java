package com.timimakkonen.minesweeper.di;

import com.timimakkonen.minesweeper.jni.AndroidMinesweeperGame;

import dagger.Module;
import dagger.Provides;

/**
 * <p>
 * Dagger module responsible for providing an instance of 'AndroidMinesweeperGame'.
 * </p>
 */
@Module
public class MinesweeperModelModule {

    // Used to load the 'libandroidminesweeper' library when this module is initialised for the first time.
    static {
        System.loadLibrary("libandroidminesweeper");
    }

    @ApplicationScope
    @Provides
    public AndroidMinesweeperGame provideAndroidMinesweeperGame() {
        return new AndroidMinesweeperGame();
    }
}
