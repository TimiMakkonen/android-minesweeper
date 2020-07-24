package com.timimakkonen.minesweeper;

import android.util.Log;


import com.timimakkonen.minesweeper.jni.AndroidMinesweeperGame;
import com.timimakkonen.minesweeper.di.ApplicationScope;
import com.timimakkonen.minesweeper.jni.vector_int;


import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

@ApplicationScope
class MinesweeperRepository {

    private static final String TAG = "MinesweeperRepository";

    private BehaviorSubject<MinesweeperDataForView> minesweeperDataForViewObservable;
    private AndroidMinesweeperGame currentMinesweeperGame;

    @Inject
    public MinesweeperRepository(AndroidMinesweeperGame androidMinesweeperGame) {

        this.currentMinesweeperGame = androidMinesweeperGame;
        this.minesweeperDataForViewObservable = BehaviorSubject.create();
        updateCurrentGridInformation();
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


    public void checkInputCoordinates(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= currentMinesweeperGame.getGridWidth() ||
            y >= currentMinesweeperGame.getGridHeight()) {
            throw new IllegalArgumentException("Trying to check cell outside the grid.");
        }
        Log.d(TAG, "checkInputCoordinates: " + String.format("Checking cell (%d, %d)", x, y));
        this.currentMinesweeperGame.checkInputCoordinates(x, y);
        updateCurrentGridInformation();
    }

    public void markInputCoordinates(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= currentMinesweeperGame.getGridWidth() ||
            y >= currentMinesweeperGame.getGridHeight()) {
            throw new IllegalArgumentException("Trying to mark cell outside the grid.");
        }
        this.currentMinesweeperGame.markInputCoordinates(x, y);
        updateCurrentGridInformation();
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
        if (gridHeight < 0 || gridWidth < 0 ) {
            throw new IllegalArgumentException("Trying to check the maximum number of mines for a negative grid.");
        }
        return AndroidMinesweeperGame.maxNumOfMines(gridHeight, gridWidth);
    }

    private void updateCurrentGridInformation() {
        final VisualMinesweeperCell[][] currentVisualMinesweeperCells =
                updateCurrentVisualMinesweeperCells();
        final boolean playerHasWon = updatePlayerHasWonInformation();
        final boolean playerHasLost = updatePlayerHasLostInformation();

        this.minesweeperDataForViewObservable.onNext(
                new MinesweeperDataForView(currentVisualMinesweeperCells, playerHasWon,
                                           playerHasLost));
    }

    private VisualMinesweeperCell[][] updateCurrentVisualMinesweeperCells() {

        Log.d(TAG, "updateCurrentVisualMinesweeperCells: Updating visual minesweeper cells");
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

}
