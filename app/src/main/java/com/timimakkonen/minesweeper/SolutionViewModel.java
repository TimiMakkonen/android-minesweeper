package com.timimakkonen.minesweeper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class SolutionViewModel extends ViewModel {

    private final MinesweeperRepository minesweeperRepository;
    private final MutableLiveData<VisualMinesweeperCell[][]> visualMinesweeperCells;
    private final CompositeDisposable disposables;

    @Inject
    public SolutionViewModel(MinesweeperRepository minesweeperRepository) {
        this.minesweeperRepository = minesweeperRepository;

        this.disposables = new CompositeDisposable();
        visualMinesweeperCells = new MutableLiveData<>();

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
                                     visualMinesweeperCells.setValue(newVisualMinesweeperCells);
                                 }

                                 @Override
                                 public void onError(@NonNull Throwable e) {

                                 }

                                 @Override
                                 public void onComplete() {

                                 }
                             }));

    }

    public LiveData<VisualMinesweeperCell[][]> getVisualMinesweeperCells() {
        return visualMinesweeperCells;
    }

    public void updateSolutionVisualisation() {
        this.minesweeperRepository.updateCurrentGridSolutionVisualisation();
    }

}