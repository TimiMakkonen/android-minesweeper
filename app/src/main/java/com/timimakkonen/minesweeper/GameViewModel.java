package com.timimakkonen.minesweeper;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

/**
 * <p>
 * This 'ViewModel' class is responsible for holding the data needed to display a minesweeper grid
 * and interacting with {@link com.timimakkonen.minesweeper.MinesweeperRepository} to forward all
 * the update requests made to it from the android/ui/view level.
 * </p>
 * <p>
 * This class has 'visualMinesweeperCells' (VisualMinesweeperCell[][]), 'playerHasWon' (Boolean),
 * 'playerHasLost' (Boolean), 'primaryActionIsCheck' (Boolean) and 'loadingInProgress' (Boolean)
 * 'LiveData's which can be observed.
 * </p>
 * <p>
 * This class itself observes 'RxJava MinesweeperDataForView Observable' and reacts to its changes
 * by updating corresponding 'LiveData's.
 * </p>
 */
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

    private static final boolean DEFAULT_PRIMARY_ACTION_IS_CHECK = true;


    //private final SavedStateHandle savedStateHandle;
    private final MinesweeperRepository minesweeperRepository;
    private final LocalStorage localStorage;
    private final BackgroundTaskRunner backgroundTaskRunner;
    private final CounterWithCallbackOnZero loadingProcessCounter;

    private final CompositeDisposable disposables;

    private final MutableLiveData<VisualMinesweeperCell[][]> visualMinesweeperCells;
    private final MutableLiveData<Boolean> playerHasWon;
    private final MutableLiveData<Boolean> playerHasLost;
    private final MutableLiveData<Boolean> primaryActionIsCheck;
    private final MutableLiveData<Boolean> loadingInProgress;


    @Inject
    public GameViewModel(/*SavedStateHandle savedStateHandle,*/
            MinesweeperRepository minesweeperRepository, LocalStorage localStorage,
            BackgroundTaskRunner backgroundTaskRunner) {
        //this.savedStateHandle = savedStateHandle;
        this.minesweeperRepository = minesweeperRepository;
        this.localStorage = localStorage;
        this.backgroundTaskRunner = backgroundTaskRunner;

        this.disposables = new CompositeDisposable();
        this.visualMinesweeperCells = new MutableLiveData<>();
        this.playerHasWon = new MutableLiveData<>();
        this.playerHasLost = new MutableLiveData<>();

        this.primaryActionIsCheck = new MutableLiveData<>(
                localStorage.getPrimActionIsCheck(DEFAULT_PRIMARY_ACTION_IS_CHECK));

        this.loadingInProgress = new MutableLiveData<>(true);

        this.loadingProcessCounter = new CounterWithCallbackOnZero(
                () -> loadingInProgress.postValue(false),
                () -> loadingInProgress.postValue(true));

        init();
    }

    private void init() {
        disposables
                .add(minesweeperRepository
                             .getCurrentVisualMinesweeperInformation()
                             .observeOn(AndroidSchedulers.mainThread())
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

    public void primaryMinesweeperCoordinatesAction(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= getCurrentGridWidth() ||
            y >= getCurrentGridHeight()) {
            throw new IllegalArgumentException(
                    "Trying perform primary action on a cell outside the grid.");
        }
        if (isCellVisible(x, y)) {
            completeAroundMinesweeperCoordinates(x, y);
        } else {
            if (primaryActionIsCheck.getValue() != null && primaryActionIsCheck.getValue()) {
                checkMinesweeperCoordinates(x, y);
            } else {
                markMinesweeperCoordinates(x, y);
            }
        }
    }

    public void secondaryMinesweeperCoordinatesAction(int x, int y)
            throws IllegalArgumentException {
        if (x < 0 || y < 0 || x >= getCurrentGridWidth() ||
            y >= getCurrentGridHeight()) {
            throw new IllegalArgumentException(
                    "Trying perform secondary action on a cell outside the grid.");
        }
        if (isCellVisible(x, y)) {
            completeAroundMinesweeperCoordinates(x, y);
        } else {
            if (primaryActionIsCheck.getValue() != null && primaryActionIsCheck.getValue()) {
                markMinesweeperCoordinates(x, y);
            } else {
                checkMinesweeperCoordinates(x, y);
            }
        }
    }

    private void checkMinesweeperCoordinates(int x, int y) {
        Log.d(TAG, "checkMinesweeperCoordinates: "
                   + String.format("Checking cell (%d, %d)", x, y));
        executeLoadingProcess(() -> minesweeperRepository.checkCoordinates(x, y));
    }

    private void markMinesweeperCoordinates(int x, int y) {
        Log.d(TAG, "markMinesweeperCoordinates: "
                   + String.format("Marking cell (%d, %d)", x, y));
        executeLoadingProcess(() -> minesweeperRepository.markCoordinates(x, y));
    }

    private void completeAroundMinesweeperCoordinates(int x, int y)
            throws IllegalArgumentException {
        if (!isCellVisible(x, y)) {
            throw new IllegalArgumentException(
                    "Trying to complete around a cell that is not visible.");
        }
        Log.d(TAG, "completeAroundMinesweeperCoordinates: "
                   + String.format("Completing around cell (%d, %d)", x, y));
        executeLoadingProcess(() -> minesweeperRepository.completeAroundCoordinates(x, y));
    }

    public void restartWithMines() {
        executeLoadingProcess(() -> minesweeperRepository.resetCurrentGame(true));
    }

    public void restartWithoutMines() {
        executeLoadingProcess(() -> minesweeperRepository.resetCurrentGame(false));
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
        executeLoadingProcess(
                () -> minesweeperRepository.startNewGame(gridHeight, gridWidth, numOfMines));
    }

    public void startNewEasyGame() {
        executeLoadingProcess(() -> minesweeperRepository
                .startNewGame(EASY_GAME_GRID_HEIGHT, EASY_GAME_GRID_WIDTH,
                              EASY_GAME_NUM_OF_MINES));
    }

    public void startNewMediumGame() {
        executeLoadingProcess(() -> minesweeperRepository
                .startNewGame(MEDIUM_GAME_GRID_HEIGHT, MEDIUM_GAME_GRID_WIDTH,
                              MEDIUM_GAME_NUM_OF_MINES));
    }


    public void startNewHardGame() {
        executeLoadingProcess(() -> minesweeperRepository
                .startNewGame(HARD_GAME_GRID_HEIGHT, HARD_GAME_GRID_WIDTH,
                              HARD_GAME_NUM_OF_MINES));
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

    private boolean isCellVisible(int x, int y) {
        if (BuildConfig.DEBUG && (x < 0 || y < 0 || x >= getCurrentGridWidth() ||
                                  y >= getCurrentGridHeight())) {
            throw new AssertionError("Trying to check visibility of a cell outside the grid.");
        }
        return minesweeperRepository.isCellVisible(x, y);
    }

    public void save() {
        executeLoadingProcess(minesweeperRepository::save);
    }

    public void switchMinesweeperPrimSecoActions() {
        if (this.primaryActionIsCheck.getValue() != null && this.primaryActionIsCheck.getValue()) {
            this.primaryActionIsCheck.setValue(false);
            this.localStorage.setPrimActionIsCheck(false);
        } else {
            this.primaryActionIsCheck.setValue(true);
            this.localStorage.setPrimActionIsCheck(true);
        }
    }

    public void setPrimaryActionIsCheckToDefault() {
        this.primaryActionIsCheck.setValue(DEFAULT_PRIMARY_ACTION_IS_CHECK);
        this.localStorage.setPrimActionIsCheck(DEFAULT_PRIMARY_ACTION_IS_CHECK);
    }

    public LiveData<Boolean> isPrimaryActionCheck() {
        return this.primaryActionIsCheck;
    }

    public LiveData<Boolean> isLoadingInProgress() {
        return this.loadingInProgress;
    }

    // executes task/process which causes UI to be notified that a task is running,
    // and also notifies UI when the task has finished running
    private void executeLoadingProcess(Runnable task) {
        loadingProcessCounter.increment();
        executeTaskOnBackground(new CallbackTask(task, loadingProcessCounter::decrement));
    }

    private void executeTaskOnBackground(Runnable task) {
        backgroundTaskRunner.execute(task);
    }
}
