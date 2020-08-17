package com.timimakkonen.minesweeper.di;

import com.timimakkonen.minesweeper.LocalStorage;
import com.timimakkonen.minesweeper.jni.AndroidMinesweeperGame;

import dagger.Module;
import dagger.Provides;

/**
 * <p>
 * Dagger module responsible for providing an instance of 'AndroidMinesweeperGame'.
 * </p>
 * <p>
 * If saved game can be found in {@link com.timimakkonen.minesweeper.LocalStorage}, and the user has chosen to resume it, this saved
 * game is returned. Else, a new instance of {@link com.timimakkonen.minesweeper.jni.AndroidMinesweeperGame} is returned.
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
    public AndroidMinesweeperGame provideAndroidMinesweeperGame(LocalStorage localStorage) {

        final boolean hasSavedGame = localStorage.getHasSavedGame(false);
        final boolean resumePreviousGame = localStorage.getSaveAndResume(true);


        if (resumePreviousGame && hasSavedGame) {
            AndroidMinesweeperGame androidMinesweeperGame = new AndroidMinesweeperGame();
            final boolean deserialisationWasSuccessful =
                    androidMinesweeperGame.deserialise(localStorage.loadCurrentMinesweeperGame());
            if (deserialisationWasSuccessful) {
                return androidMinesweeperGame;
            } else {
                localStorage.setSaveWasCorrupted(true);
                localStorage.deleteCurrentMinesweeperGame();
                localStorage.setHasSavedGame(false);
            }
        }
        // return this default AndroidMinesweeperGame if no save can be found, or if the save is corrupted
        return new AndroidMinesweeperGame(10, 10, 20);
    }
}
