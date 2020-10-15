package com.timimakkonen.minesweeper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

/**
 * <p>
 * This 'ViewModel' class is responsible for holding the data needed to display a solution
 * visualisation of a minesweeper grid and interacting with {@link MinesweeperRepository} to forward
 * all the update requests made to it from the android/ui/view level.
 * </p>
 * <p>
 * This class has 'visualMinesweeperCells' (VisualMinesweeperCell[][]) and 'loadingInProgress'
 * (Boolean) 'LiveData's which can be observed.
 * </p>
 * <p>
 * This class itself observes 'RxJava MinesweeperDataForView Observable' and reacts to its changes
 * by updating its corresponding 'LiveData'.
 * </p>
 */
public class SolutionViewModel extends ViewModel {

    private final MinesweeperRepository minesweeperRepository;
    private final BackgroundTaskRunner backgroundTaskRunner;

    private final CompositeDisposable disposables;

    private final MutableLiveData<VisualMinesweeperCell[][]> visualMinesweeperCells;
    private final MutableLiveData<Boolean> loadingInProgress;

    @Inject
    public SolutionViewModel(MinesweeperRepository minesweeperRepository,
                             BackgroundTaskRunner backgroundTaskRunner) {
        this.minesweeperRepository = minesweeperRepository;
        this.backgroundTaskRunner = backgroundTaskRunner;

        this.disposables = new CompositeDisposable();
        visualMinesweeperCells = new MutableLiveData<>();

        this.loadingInProgress = new MutableLiveData<>(false);

        init();
    }

    private void init() {
        disposables
                .add(minesweeperRepository
                             .getCurrentVisualMinesweeperSolutionInformation()
                             .subscribeWith(new DisposableObserver<VisualMinesweeperCell[][]>() {
                                 @Override
                                 public void onNext(
                                         @NonNull VisualMinesweeperCell[][] newVisualMinesweeperCells) {
                                     visualMinesweeperCells.postValue(newVisualMinesweeperCells);
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

    public LiveData<Boolean> isLoadingInProgress() {
        return this.loadingInProgress;
    }

    public void updateSolutionVisualisation() {
        executeLoadingProcess(minesweeperRepository::updateCurrentGridSolutionVisualisation);
    }

    // executes task/process which causes UI to be notified that a task is running,
    // and also notifies UI when the task has finished running
    private void executeLoadingProcess(Runnable task) {
        loadingInProgress.setValue(true);
        executeTaskOnBackground(new CallbackTask(task, () -> loadingInProgress.postValue(false)));
    }

    private void executeTaskOnBackground(Runnable task) {
        backgroundTaskRunner.execute(task);
    }

}