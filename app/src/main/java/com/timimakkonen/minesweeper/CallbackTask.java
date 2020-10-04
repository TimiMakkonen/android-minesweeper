package com.timimakkonen.minesweeper;


/**
 * <p>
 * This class combines two 'Runnable's (task and callback) together to form a new 'Runnable'.
 * </p>
 */
public class CallbackTask implements Runnable {

    private final Runnable task;
    private final Runnable callback;

    public CallbackTask(Runnable task, Runnable callback) {
        this.task = task;
        this.callback = callback;
    }

    @Override
    public void run() {
        task.run();
        callback.run();
    }
}
