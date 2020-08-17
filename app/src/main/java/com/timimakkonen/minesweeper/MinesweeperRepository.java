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
 * This class has 'minesweeperDataForViewObservable' (MinesweeperDataForView) and
 * 'minesweeperSolutionVisualisationObservable' (VisualMinesweeperCell[][]) 'BehaviorSubject's,
 * which can be observed.
 * </p>
 */
@ApplicationScope
class MinesweeperRepository {

    private static final String TAG = "MinesweeperRepository";

    private final LocalStorage localStorage;
    private final BehaviorSubject<MinesweeperDataForView> minesweeperDataForViewObservable;
    private final BehaviorSubject<VisualMinesweeperCell[][]>
            minesweeperSolutionVisualisationObservable;
    private final AndroidMinesweeperGame currentMinesweeperGame;

    private boolean solutionVisualisationIsOutdated;

    @Inject
    public MinesweeperRepository(LocalStorage localStorage,
                                 AndroidMinesweeperGame androidMinesweeperGame) {

        this.localStorage = localStorage;
        this.currentMinesweeperGame = androidMinesweeperGame;
        this.minesweeperDataForViewObservable = BehaviorSubject.create();
        this.minesweeperSolutionVisualisationObservable = BehaviorSubject.create();
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

    public Observable<MinesweeperDataForView> getCurrentVisualMinesweeperInformation() {

        return this.minesweeperDataForViewObservable;
    }

    public Observable<VisualMinesweeperCell[][]> getCurrentVisualMinesweeperSolutionInformation() {

        return this.minesweeperSolutionVisualisationObservable;
    }

    public void checkCoordinates(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= currentMinesweeperGame.getGridWidth() ||
            y >= currentMinesweeperGame.getGridHeight()) {
            throw new IllegalArgumentException("Trying to check cell outside the grid.");
        }
        Log.d(TAG, "checkCoordinates: " + String.format("Checking cell (%d, %d)", x, y));
        this.currentMinesweeperGame.checkInputCoordinates(x, y);
        updateCurrentGridInformation();
    }

    public void markCoordinates(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= currentMinesweeperGame.getGridWidth() ||
            y >= currentMinesweeperGame.getGridHeight()) {
            throw new IllegalArgumentException("Trying to mark cell outside the grid.");
        }
        this.currentMinesweeperGame.markInputCoordinates(x, y);
        updateCurrentGridInformation();
    }

    public void completeAroundCoordinates(int x, int y) throws IllegalArgumentException {
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

    public boolean isCellVisible(int x, int y) {
        if (x < 0 || y < 0 || x >= currentMinesweeperGame.getGridWidth() ||
            y >= currentMinesweeperGame.getGridHeight()) {
            throw new IllegalArgumentException(
                    "Trying to check visibility of a cell outside the grid.");
        }
        return this.currentMinesweeperGame.isCellVisible(x, y);
    }

    public void resetCurrentGame(boolean keepCreatedMines) {
        this.currentMinesweeperGame.reset(keepCreatedMines);
        updateCurrentGridInformation();
    }

    public void startNewGame(int gridHeight, int gridWidth,
                             int numOfMines) throws IllegalArgumentException {
        verifyGridDimension(gridHeight);
        verifyGridDimension(gridWidth);
        verifyNumOfMines(gridHeight, gridWidth, numOfMines);
        this.currentMinesweeperGame.newGame(gridHeight, gridWidth, numOfMines);
        updateCurrentGridInformation();
    }

    @SuppressWarnings("unused")
    public void startNewGame(int gridHeight, int gridWidth,
                             double proportionOfMines) throws IllegalArgumentException {
        verifyGridDimension(gridHeight);
        verifyGridDimension(gridWidth);
        verifyProportionOfMines(gridHeight, gridWidth, proportionOfMines);
        this.currentMinesweeperGame.newGame(gridHeight, gridWidth, proportionOfMines);
        updateCurrentGridInformation();
    }

    public int minNumOfMines() {
        return AndroidMinesweeperGame.minNumOfMines();
    }

    public int maxNumOfMines(int gridHeight, int gridWidth) throws IllegalArgumentException {
        if (gridHeight < 0 || gridWidth < 0) {
            throw new IllegalArgumentException(
                    "Trying to check the maximum number of mines for a negative grid.");
        }
        return AndroidMinesweeperGame.maxNumOfMines(gridHeight, gridWidth);
    }

    public void save() {
        saveCurrentMinesweeperGame();
    }

    private void saveCurrentMinesweeperGame() {
        localStorage.saveCurrentMinesweeperGame(this.currentMinesweeperGame.serialise());
        localStorage.setHasSavedGame(true);
    }

    private void updateCurrentGridInformation() {
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

    public void updateCurrentGridSolutionVisualisation() {
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
