package com.timimakkonen.minesweeper;

import android.util.Log;

import com.timimakkonen.minesweeper.jni.AndroidMinesweeperGame;
import com.timimakkonen.minesweeper.di.ApplicationScope;
import com.timimakkonen.minesweeper.jni.vector_int;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

/**
 * <p>
 * This class is the main holder of the {@link AndroidMinesweeperGame}.
 * </p>
 * <p>
 * This class holds an instance of {@link AndroidMinesweeperGame} and handles calling its methods,
 * updates visual information when needed (into its observables) and saves the game progress on
 * request.
 * </p>
 * <p>
 * This class has 'minesweeperDataForViewObservable' (MinesweeperDataForView),
 * 'minesweeperSolutionVisualisationObservable' (VisualMinesweeperCell[][]) and
 * 'saveFileIsCorruptedObservable' (Boolean) 'BehaviorSubject's, which can be observed.
 * </p>
 * <p>
 * This class is thread-safe as long as {@link LocalStorage} and {@link AndroidMinesweeperGame}
 * provided to it are.
 * </p>
 */
@ApplicationScope
class MinesweeperRepository {

    private static final String TAG = "MinesweeperRepository";

    private final LocalStorage localStorage;
    private final AndroidMinesweeperGame currentMinesweeperGame;

    private final BehaviorSubject<MinesweeperDataForView> minesweeperDataForViewObservable;
    private final BehaviorSubject<VisualMinesweeperCell[][]>
            minesweeperSolutionVisualisationObservable;
    private final BehaviorSubject<Boolean> saveFileIsCorruptedObservable;

    private boolean solutionVisualisationIsOutdated;

    @Inject
    public MinesweeperRepository(LocalStorage localStorage,
                                 AndroidMinesweeperGame androidMinesweeperGame) {

        this.localStorage = localStorage;
        this.currentMinesweeperGame = androidMinesweeperGame;

        this.minesweeperDataForViewObservable = BehaviorSubject.create();
        this.minesweeperSolutionVisualisationObservable = BehaviorSubject.create();
        this.saveFileIsCorruptedObservable = BehaviorSubject.create();

        updateCurrentGridInformation();
        solutionVisualisationIsOutdated = true;
    }

    private static void verifyGridDimension(int gridDimension) throws IllegalArgumentException {
        if (gridDimension < 0) {
            throw new IllegalArgumentException(
                    "Trying to initialise a new grid with negative grid dimension.");
        }
    }

    private static void verifyNumOfMines(int gridHeight, int gridWidth,
                                         int numOfMines) throws IllegalArgumentException {
        if (numOfMines < AndroidMinesweeperGame.minNumOfMines()) {
            throw new IllegalArgumentException(
                    "Trying to initialise a new grid with too few mines.");
        } else if (numOfMines > AndroidMinesweeperGame.maxNumOfMines(gridHeight, gridWidth)) {
            throw new IllegalArgumentException(
                    "Trying to initialise a new grid with too many mines.");
        }
    }

    private static void verifyProportionOfMines(int gridHeight, int gridWidth,
                                                double proportionOfMines)
            throws IllegalArgumentException {
        if (proportionOfMines < AndroidMinesweeperGame.minProportionOfMines()) {
            throw new IllegalArgumentException(
                    "Trying to initialise a new grid with too few mines.");
        } else if (proportionOfMines > AndroidMinesweeperGame.maxProportionOfMines(gridHeight,
                                                                                   gridWidth)) {
            throw new IllegalArgumentException(
                    "Trying to initialise a new grid with too many mines.");
        }
    }

    public synchronized Observable<MinesweeperDataForView> getCurrentVisualMinesweeperInformation() {
        return this.minesweeperDataForViewObservable;
    }

    public synchronized Observable<VisualMinesweeperCell[][]> getCurrentVisualMinesweeperSolutionInformation() {
        return this.minesweeperSolutionVisualisationObservable;
    }

    public synchronized Observable<Boolean> isSaveFileCorrupted() {
        return this.saveFileIsCorruptedObservable;
    }

    public synchronized void checkCoordinates(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= currentMinesweeperGame.getGridWidth() ||
            y >= currentMinesweeperGame.getGridHeight()) {
            throw new IllegalArgumentException("Trying to check cell outside the grid.");
        }
        Log.d(TAG, "checkCoordinates: " + String.format("Checking cell (%d, %d)", x, y));
        this.currentMinesweeperGame.checkInputCoordinates(x, y);
        updateCurrentGridInformation();
    }

    public synchronized void markCoordinates(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= currentMinesweeperGame.getGridWidth() ||
            y >= currentMinesweeperGame.getGridHeight()) {
            throw new IllegalArgumentException("Trying to mark cell outside the grid.");
        }
        this.currentMinesweeperGame.markInputCoordinates(x, y);
        updateCurrentGridInformation();
    }

    public synchronized void completeAroundCoordinates(int x, int y)
            throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= currentMinesweeperGame.getGridWidth() ||
            y >= currentMinesweeperGame.getGridHeight()) {
            throw new IllegalArgumentException(
                    "Trying to complete around a cell outside the grid.");
        }
        if (!currentMinesweeperGame.isCellVisible(x, y)) {
            throw new IllegalArgumentException(
                    "Trying to complete around a cell that is not visible.");
        }
        this.currentMinesweeperGame.completeAroundInputCoordinates(x, y);
        updateCurrentGridInformation();
    }

    public synchronized boolean isCellVisible(int x, int y) {
        if (x < 0 || y < 0 || x >= currentMinesweeperGame.getGridWidth() ||
            y >= currentMinesweeperGame.getGridHeight()) {
            throw new IllegalArgumentException(
                    "Trying to check visibility of a cell outside the grid.");
        }
        return this.currentMinesweeperGame.isCellVisible(x, y);
    }

    public synchronized void resetCurrentGame(boolean keepCreatedMines) {
        this.currentMinesweeperGame.reset(keepCreatedMines);
        updateCurrentGridInformation();
    }

    public synchronized void startNewGame(int gridHeight, int gridWidth,
                                          int numOfMines) throws IllegalArgumentException {
        verifyGridDimension(gridHeight);
        verifyGridDimension(gridWidth);
        verifyNumOfMines(gridHeight, gridWidth, numOfMines);
        this.currentMinesweeperGame.newGame(gridHeight, gridWidth, numOfMines);
        updateCurrentGridInformation();
    }


    @SuppressWarnings("unused")
    public synchronized void startNewGame(int gridHeight, int gridWidth,
                                          double proportionOfMines)
            throws IllegalArgumentException {
        verifyGridDimension(gridHeight);
        verifyGridDimension(gridWidth);
        verifyProportionOfMines(gridHeight, gridWidth, proportionOfMines);
        this.currentMinesweeperGame.newGame(gridHeight, gridWidth, proportionOfMines);
        updateCurrentGridInformation();
    }

    public synchronized int minNumOfMines() {
        return AndroidMinesweeperGame.minNumOfMines();
    }

    public synchronized int maxNumOfMines(int gridHeight, int gridWidth)
            throws IllegalArgumentException {
        if (gridHeight < 0 || gridWidth < 0) {
            throw new IllegalArgumentException(
                    "Trying to check the maximum number of mines for a negative grid.");
        }
        return AndroidMinesweeperGame.maxNumOfMines(gridHeight, gridWidth);
    }

    public synchronized void save() {
        saveCurrentMinesweeperGame();
    }

    /**
     * Loads a game from save file, if a save file exists and this option has been turned on. Else
     * loads new default game.
     *
     * @return Loading of the save file was successful.
     */
    @SuppressWarnings("UnusedReturnValue")
    public synchronized boolean load() {
        Log.d(TAG, String.format("load: Current thread is: %s", Thread.currentThread()));
        boolean loadWasSuccessful = loadCurrentMinesweeperGame();
        updateCurrentGridInformation();
        return loadWasSuccessful;
    }

    private void saveCurrentMinesweeperGame() {
        localStorage.saveCurrentMinesweeperGame(this.currentMinesweeperGame.serialise());
        localStorage.setHasSavedGame(true);
    }

    private boolean loadCurrentMinesweeperGame() {

        if (localStorage.getHasSavedGame(false)) {
            final boolean deserialisationWasSuccessful =
                    currentMinesweeperGame.deserialise(localStorage.loadCurrentMinesweeperGame());
            if (deserialisationWasSuccessful) {
                return true;
            } else {
                Log.d(TAG, "loadCurrentMinesweeperGame: Save file was corrupted");
                saveFileIsCorruptedObservable.onNext(true);
                localStorage.deleteCurrentMinesweeperGame();
                localStorage.setHasSavedGame(false);
                saveFileIsCorruptedObservable.onNext(false);

                // With the current version of minesweeper-library (v8.5.2),
                //  failed deserialisation will probably cause original game to get corrupted.
                // Hence we must start a new game.
                startDefaultNewGame();
            }
        }
        return false;
    }

    private void startDefaultNewGame() {
        this.currentMinesweeperGame.newGame(10, 10, 20);
        updateCurrentGridInformation();
    }

    private void updateCurrentGridInformation() {
        Log.d(TAG, String.format("updateCurrentGridInformation: Current thread is: %s",
                                 Thread.currentThread()));
        final VisualMinesweeperCell[][] currentVisualMinesweeperCells =
                getCurrentVisualMinesweeperCells();
        final boolean playerHasWon = updatePlayerHasWonInformation();
        final boolean playerHasLost = updatePlayerHasLostInformation();

        this.minesweeperDataForViewObservable.onNext(
                new MinesweeperDataForView(currentVisualMinesweeperCells, playerHasWon,
                                           playerHasLost));

        solutionVisualisationIsOutdated = true;
    }

    private VisualMinesweeperCell[][] getCurrentVisualMinesweeperCells() {

        Log.d(TAG, "getCurrentVisualMinesweeperCells: Updating visual minesweeper cells");
        final int gridHeight = currentMinesweeperGame.getGridHeight();
        final int gridWidth = currentMinesweeperGame.getGridWidth();

        VisualMinesweeperCell[][] newVisualMinesweeperCells =
                new VisualMinesweeperCell[gridHeight][gridWidth];
        vector_int currentGameJniVisualisation = currentMinesweeperGame.visualise();

        int i = 0;
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; i++, x++) {
                newVisualMinesweeperCells[y][x] =
                        VisualMinesweeperCell.newVisualMinesweeperCell(
                                currentGameJniVisualisation.get(i));
            }
        }

        return newVisualMinesweeperCells;
    }

    private boolean updatePlayerHasWonInformation() {
        return this.currentMinesweeperGame.playerHasWon();
    }

    private boolean updatePlayerHasLostInformation() {
        return this.currentMinesweeperGame.playerHasLost();
    }

    public synchronized void updateCurrentGridSolutionVisualisation() {
        if (solutionVisualisationIsOutdated) {
            this.minesweeperSolutionVisualisationObservable.onNext(
                    getCurrentSolutionVisualisation());
            solutionVisualisationIsOutdated = false;
        }
    }


    private VisualMinesweeperCell[][] getCurrentSolutionVisualisation() {

        Log.d(TAG,
              "getCurrentSolutionVisualisation: Updating minesweeper solution visualisation cells");
        final int gridHeight = currentMinesweeperGame.getGridHeight();
        final int gridWidth = currentMinesweeperGame.getGridWidth();

        VisualMinesweeperCell[][] newVisualSolutionMinesweeperCells =
                new VisualMinesweeperCell[gridHeight][gridWidth];
        vector_int currentGameJniSolutionVisualisation = currentMinesweeperGame.visualiseSolution();

        int i = 0;
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; i++, x++) {
                newVisualSolutionMinesweeperCells[y][x] =
                        VisualMinesweeperCell.newVisualMinesweeperCell(
                                currentGameJniSolutionVisualisation.get(i));
            }
        }

        return newVisualSolutionMinesweeperCells;
    }
}
