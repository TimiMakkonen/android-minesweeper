package com.timimakkonen.minesweeper;

import com.timimakkonen.minesweeper.di.ApplicationScope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

/**
 * TODO
 */
@ApplicationScope
public class BackgroundTaskRunner {

    private final ExecutorService executorService;

    @Inject
    public BackgroundTaskRunner() {

        executorService = Executors.newSingleThreadExecutor();
    }

    public void execute(Runnable task) {
        executorService.execute(task);
    }

    public void execute(Runnable task, Runnable callback) {
        executorService.execute(new CallbackTask(task, callback));
    }
}
