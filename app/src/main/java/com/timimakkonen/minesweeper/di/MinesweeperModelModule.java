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
 * If saved game can be found from 'LocalStorage', and the user has chosen to resume it, this saved
 * game is returned. Else, a new instance of 'AndroidMinesweeperGame' is returned.
 * </p>
 */
@Module
public class MinesweeperModelModule {

    private static final String HAS_SAVED_GAME_KEY = "has_saved_game";
    private static final String SAVE_AND_RESUME_KEY = "save_and_resume";
    private static final String SAVE_WAS_CORRUPTED_KEY = "save_was_corrupted";

    // Used to load the 'libandroidminesweeper' library when this module is initialised for the first time.
    static {
        System.loadLibrary("libandroidminesweeper");
    }

    @ApplicationScope
    @Provides
    public AndroidMinesweeperGame provideAndroidMinesweeperGame(LocalStorage localStorage) {

        final boolean hasSavedGame = localStorage.getBoolean(HAS_SAVED_GAME_KEY, false);
        final boolean resumePreviousGame = localStorage.getBoolean(SAVE_AND_RESUME_KEY, true);

        if (resumePreviousGame && hasSavedGame) {
            AndroidMinesweeperGame androidMinesweeperGame = new AndroidMinesweeperGame();
            final boolean deserialisationWasSuccessful =
                    androidMinesweeperGame.deserialise(localStorage.loadCurrentMinesweeperGame());
            if (deserialisationWasSuccessful) {
                return androidMinesweeperGame;
            } else {
                localStorage.setBoolean(SAVE_WAS_CORRUPTED_KEY, true);
                localStorage.deleteCurrentMinesweeperGame();
                localStorage.setBoolean(HAS_SAVED_GAME_KEY, false);
            }
        }
        // return this default AndroidMinesweeperGame if no save can be found, or if the save is corrupted
        return new AndroidMinesweeperGame(10, 10, 20);
    }
}
