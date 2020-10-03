package com.timimakkonen.minesweeper;

/**
 * TODO
 */
public class CounterWithCallbackOnZero {

    private long counter = 0;
    private final Runnable callbackOnZero;
    private final Runnable callbackOnChangeFromZero;


    public CounterWithCallbackOnZero(Runnable callbackOnZero, Runnable callbackOnChangeFromZero) {
        this.callbackOnZero = callbackOnZero;
        this.callbackOnChangeFromZero = callbackOnChangeFromZero;

        callbackOnZero.run();
    }

    public synchronized void increment() {
        if (counter == 0) {
            callbackOnChangeFromZero.run();
        }
        ++counter;
        if (counter == 0) {
            callbackOnZero.run();
        }
    }

    public synchronized void decrement() {
        if (counter == 0) {
            callbackOnChangeFromZero.run();
        }
        --counter;
        if (counter == 0) {
            callbackOnZero.run();
        }
    }
}