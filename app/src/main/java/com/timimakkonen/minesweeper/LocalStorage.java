package com.timimakkonen.minesweeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.timimakkonen.minesweeper.di.ApplicationScope;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

/**
 * <p>
 * This class is responsible for the local storage. The idea is to separate the concept of storage
 * from the android specific details, such as 'SharedPreferences' and android file save path.
 * </p>
 * <p>
 * This class stores 'currentMinesweeperGame' as a file , and all other keys as a
 * 'SharedPreference'. Moreover, 'currentMinesweeperGame' has its own save, load and delete methods,
 * while all other keys have explicit getters and setters.
 * </p>
 */
@ApplicationScope
public class LocalStorage {

    private static final String TAG = "LocalStorage";

    // save file names:
    private static final String CURRENT_GAME_SAVE_FILE_NAME = "current_minesweeper_game.save";

    // preference keys:
    private static final String SAVE_AND_RESUME_KEY = "save_and_resume";
    private static final String USE_PRIM_SECO_SWITCH_KEY = "use_prim_seco_switch";
    private static final String PRIM_SECO_SWITCH_HORIZ_BIAS_KEY =
            "prim_seco_switch_horizontal_bias";
    private static final String PRIM_SECO_SWITCH_CUSTOM_HORIZ_BIAS_KEY =
            "prim_seco_switch_horizontal_bias_custom";
    private static final String USE_NIGHT_MODE_KEY = "use_night_mode";
    private static final String OVERRIDE_SYSTEM_DARK_THEME_KEY = "override_system_dark_theme";
    private static final String HAS_SAVED_GAME_KEY = "has_saved_game";
    private static final String PRIM_ACTION_IS_CHECK_KEY = "prim_action_is_check";
    private static final String SAVE_WAS_CORRUPTED_KEY = "save_was_corrupted";

    private final File savePath;
    private final SharedPreferences sharedPrefs;


    @Inject
    public LocalStorage(Context context) {

        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        this.savePath = new File(context.getFilesDir(), "save");
        if (!savePath.exists()) {
            //noinspection ResultOfMethodCallIgnored
            savePath.mkdirs();
        }
    }

    // +------------------------+
    // | explicit file methods: |
    // +------------------------+

    public void saveCurrentMinesweeperGame(String strCurrentMinesweeperGame) {
        saveToFile(CURRENT_GAME_SAVE_FILE_NAME, strCurrentMinesweeperGame);
    }

    public String loadCurrentMinesweeperGame() {
        return loadFromFile(CURRENT_GAME_SAVE_FILE_NAME);
    }

    public void deleteCurrentMinesweeperGame() {
        deleteSaveFile(CURRENT_GAME_SAVE_FILE_NAME);
    }

    // +---------------+
    // | file methods: |
    // +---------------+

    private void saveToFile(@SuppressWarnings("SameParameterValue") String saveFileName,
                            String strToSave) {

        try {
            File file = new File(savePath, saveFileName);
            Log.d(TAG, String.format("save: Saving file '%s' to '%s'.", saveFileName,
                                     file.getAbsolutePath()));
            FileWriter writer = new FileWriter(file);
            writer.write(strToSave);
            writer.close();
            Log.d(TAG, String.format("save: File '%s' has been saved to '%s'.", saveFileName,
                                     file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadFromFile(@SuppressWarnings("SameParameterValue") String saveFileName) {
        String output = "";
        StringBuilder stringBuilder = new StringBuilder();
        FileInputStream fis;
        try {
            fis = new FileInputStream(new File(savePath, saveFileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fis,
                                                                        StandardCharsets.UTF_8);

            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                output = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return output;
    }

    private void deleteSaveFile(@SuppressWarnings("SameParameterValue") String saveFileName) {
        File file = new File(savePath, saveFileName);
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    // +-----------------------------+
    // | shared preferences methods: |
    // +-----------------------------+

    @SuppressWarnings("SameParameterValue")
    private String getString(String key, String defValue) {
        return sharedPrefs.getString(key, defValue);
    }

    private boolean getBoolean(String key, boolean defValue) {
        return sharedPrefs.getBoolean(key, defValue);
    }

    private void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    @SuppressWarnings("SameParameterValue")
    private int getInt(String key, int defValue) {
        return sharedPrefs.getInt(key, defValue);
    }

    // +--------------------------------------+
    // | explicit shared preferences methods: |
    // +--------------------------------------+

    // booleans:

    public boolean getUsePrimSecoSwitchKey(boolean defValue) {
        return getBoolean(USE_PRIM_SECO_SWITCH_KEY, defValue);
    }

    public boolean getPrimActionIsCheck(boolean defValue) {
        return getBoolean(PRIM_ACTION_IS_CHECK_KEY, defValue);
    }

    public void setPrimActionIsCheck(boolean value) {
        setBoolean(PRIM_ACTION_IS_CHECK_KEY, value);
    }

    public boolean getHasSavedGame(boolean defValue) {
        return getBoolean(HAS_SAVED_GAME_KEY, defValue);
    }

    public void setHasSavedGame(boolean value) {
        setBoolean(HAS_SAVED_GAME_KEY, value);
    }

    public boolean getSaveAndResume(boolean defValue) {
        return getBoolean(SAVE_AND_RESUME_KEY, defValue);
    }


    public boolean getSaveWasCorrupted(boolean defValue) {
        return getBoolean(SAVE_WAS_CORRUPTED_KEY, defValue);
    }

    public void setSaveWasCorrupted(boolean value) {
        setBoolean(SAVE_WAS_CORRUPTED_KEY, value);
    }

    public boolean getOverrideSystemDarkTheme(boolean defValue) {
        return getBoolean(OVERRIDE_SYSTEM_DARK_THEME_KEY, defValue);
    }

    public boolean getUseNightMode(boolean defValue) {
        return getBoolean(USE_NIGHT_MODE_KEY, defValue);
    }

    // ints:

    public int getPrimSecoSwitchCustomHorizBias(int defValue) {
        return getInt(PRIM_SECO_SWITCH_CUSTOM_HORIZ_BIAS_KEY, defValue);
    }


    // strings:

    public String getPrimSecoSwitchHorizBias(String defValue) {
        return getString(PRIM_SECO_SWITCH_HORIZ_BIAS_KEY, defValue);
    }

}
