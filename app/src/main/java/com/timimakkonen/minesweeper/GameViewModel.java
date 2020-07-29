package com.timimakkonen.minesweeper;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class GameViewModel extends ViewModel {

    private static final String TAG = "GameViewModel";

    private static final int EASY_GAME_GRID_HEIGHT = 9;
    private static final int EASY_GAME_GRID_WIDTH = 9;
    private static final int EASY_GAME_NUM_OF_MINES = 10;
    private static final int MEDIUM_GAME_GRID_HEIGHT = 16;
    private static final int MEDIUM_GAME_GRID_WIDTH = 16;
    private static final int MEDIUM_GAME_NUM_OF_MINES = 40;
    private static final int HARD_GAME_GRID_HEIGHT = 30;
    private static final int HARD_GAME_GRID_WIDTH = 16;
    private static final int HARD_GAME_NUM_OF_MINES = 99;


    //private final SavedStateHandle savedStateHandle;
    private final MinesweeperRepository minesweeperRepository;
    private final MutableLiveData<VisualMinesweeperCell[][]> visualMinesweeperCells;
    private final MutableLiveData<Boolean> playerHasWon;
    private final MutableLiveData<Boolean> playerHasLost;
    private final CompositeDisposable disposables;


    @Inject
    public GameViewModel(/*SavedStateHandle savedStateHandle,*/
            MinesweeperRepository minesweeperRepository) {
        //this.savedStateHandle = savedStateHandle;
        this.minesweeperRepository = minesweeperRepository;

        this.disposables = new CompositeDisposable();
        visualMinesweeperCells = new MutableLiveData<>();
        playerHasWon = new MutableLiveData<>();
        playerHasLost = new MutableLiveData<>();

        init();
    }

    private void init() {
        disposables
                .add(minesweeperRepository
                             .getCurrentVisualMinesweeperInformation()
                             .subscribeWith(new DisposableObserver<MinesweeperDataForView>() {
                                 @Override
                                 public void onNext(
                                         @NonNull MinesweeperDataForView minesweeperDataForView) {
                                     visualMinesweeperCells.setValue(minesweeperDataForView
                                                                             .getCurrentVisualMinesweeperCells());

                                     Boolean playerHasWonBool =
                                             GameViewModel.this.playerHasWon.getValue();
                                     if (playerHasWonBool == null || (playerHasWonBool !=
                                                                      minesweeperDataForView
                                                                              .hasPlayerWon())) {
                                         GameViewModel.this.playerHasWon.setValue(
                                                 minesweeperDataForView.hasPlayerWon());
                                     }

                                     Boolean playerHasLostBool =
                                             GameViewModel.this.playerHasLost.getValue();
                                     if (playerHasLostBool == null || (playerHasLostBool !=
                                                                       minesweeperDataForView
                                                                               .hasPlayerLost())) {
                                         GameViewModel.this.playerHasLost.setValue(
                                                 minesweeperDataForView.hasPlayerLost());
                                     }

                                 }

                                 @Override
                                 public void onError(@NonNull Throwable e) {

                                 }

                                 @Override
                                 public void onComplete() {

                                 }
                             }));

    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public LiveData<VisualMinesweeperCell[][]> getVisualMinesweeperCells() {
        return visualMinesweeperCells;
    }

    public LiveData<Boolean> hasPlayerWon() {
        return playerHasWon;
    }

    public LiveData<Boolean> hasPlayerLost() {
        return playerHasLost;
    }

    public void checkMinesweeperInputCoordinates(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= getCurrentGridWidth() ||
            y >= getCurrentGridHeight()) {
            throw new IllegalArgumentException("Trying to check cell outside the grid.");
        }
        Log.d(TAG, "checkMinesweeperInputCoordinates: "
                   + String.format("Checking cell (%d, %d)", x, y));
        minesweeperRepository.checkInputCoordinates(x, y);
    }

    public void markMinesweeperInputCoordinates(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= getCurrentGridWidth() ||
            y >= getCurrentGridHeight()) {
            throw new IllegalArgumentException("Trying to mark cell outside the grid.");
        }

        minesweeperRepository.markInputCoordinates(x, y);
    }

    public void restartWithMines() {
        minesweeperRepository.resetCurrentGame(true);
    }

    public void restartWithoutMines() {
        minesweeperRepository.resetCurrentGame(false);
    }

    public void startNewGame(int gridHeight, int gridWidth, int numOfMines)
            throws IllegalArgumentException {
        if (gridHeight < 0 || gridWidth < 0) {
            throw new IllegalArgumentException(
                    "Trying to initialise a new grid with negative grid dimension.");
        }
        if (numOfMines < minNumOfMines()) {
            throw new IllegalArgumentException(
                    "Trying to initialise a new grid with too few mines.");
        }
        if (numOfMines > maxNumOfMines(gridHeight, gridWidth)) {
            throw new IllegalArgumentException(
                    "Trying to initialise a new grid with too many mines");
        }
        minesweeperRepository.startNewGame(gridHeight, gridWidth, numOfMines);
    }

    public void startNewEasyGame() {
        minesweeperRepository.startNewGame(EASY_GAME_GRID_HEIGHT, EASY_GAME_GRID_WIDTH,
                                           EASY_GAME_NUM_OF_MINES);
    }

    public void startNewMediumGame() {
        minesweeperRepository.startNewGame(MEDIUM_GAME_GRID_HEIGHT, MEDIUM_GAME_GRID_WIDTH,
                                           MEDIUM_GAME_NUM_OF_MINES);
    }


    public void startNewHardGame() {
        minesweeperRepository.startNewGame(HARD_GAME_GRID_HEIGHT, HARD_GAME_GRID_WIDTH,
                                           HARD_GAME_NUM_OF_MINES);
    }

    public int maxNumOfMines(int gridHeight, int gridWidth) throws IllegalArgumentException {
        if (gridHeight < 0 || gridWidth < 0) {
            throw new IllegalArgumentException(
                    "Trying to check the maximum number of mines for a negative grid.");
        }
        return minesweeperRepository.maxNumOfMines(gridHeight, gridWidth);
    }

    private int minNumOfMines() {
        return minesweeperRepository.minNumOfMines();
    }

    private int getCurrentGridHeight() {
        return Objects.requireNonNull(visualMinesweeperCells.getValue()).length;
    }

    private int getCurrentGridWidth() {
        if (Objects.requireNonNull(visualMinesweeperCells.getValue()).length < 1) {
            return 0;
        } else {
            return visualMinesweeperCells.getValue()[0].length;
        }
    }

    public void save() {
        minesweeperRepository.save();
    }
}
