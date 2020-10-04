package com.timimakkonen.minesweeper;

import com.timimakkonen.minesweeper.di.ApplicationScope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

/**
 * <p>
 * This class takes care of running tasks on background.
 * </p>
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

    @SuppressWarnings("unused")
    public void execute(Runnable task, Runnable callback) {
        executorService.execute(new CallbackTask(task, callback));
    }
}
