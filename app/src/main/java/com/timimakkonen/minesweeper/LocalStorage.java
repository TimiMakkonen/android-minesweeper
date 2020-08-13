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
 * This class stores 'currentMinesweeperGame' as a file , and any other given keys as a
 * 'SharedPreference'. Moreover, 'currentMinesweeperGame' has its own save, load and delete methods,
 * while all other keys are specified outside this class. This is inconsistent and questionable at
 * best. Hence, rework of this class to go in either direction is under consideration.
 * </p>
 */
@ApplicationScope
public class LocalStorage {

    private static final String TAG = "LocalStorage";
    private static final String CURRENT_GAME_SAVE_FILE_NAME = "current_minesweeper_game.save";

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


    public void saveCurrentMinesweeperGame(String strCurrentMinesweeperGame) {
        saveToFile(CURRENT_GAME_SAVE_FILE_NAME, strCurrentMinesweeperGame);
    }

    public String loadCurrentMinesweeperGame() {
        return loadFromFile(CURRENT_GAME_SAVE_FILE_NAME);
    }

    public void deleteCurrentMinesweeperGame() {
        deleteSaveFile(CURRENT_GAME_SAVE_FILE_NAME);
    }

    private void saveToFile(@SuppressWarnings("SameParameterValue") String saveFileName,
                            String strToSave) {

        try {
            File file = new File(savePath, saveFileName);
            Log.d(TAG, String.format("save: Saving file '%s' to '%s'.", saveFileName,
                                     file.getAbsolutePath()));
            FileWriter writer = new FileWriter(file);
            writer.write(strToSave);
            writer.close();
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

    public String getString(String key, String defValue) {
        return sharedPrefs.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPrefs.getBoolean(key, defValue);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int getInt(String key, int defValue) {
        return sharedPrefs.getInt(key, defValue);
    }
}
