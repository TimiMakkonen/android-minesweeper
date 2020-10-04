package com.timimakkonen.minesweeper;

/**
 * <p>
 * This class makes a callback when its internal counter reaches zero and another callback when its
 * counter turns non-zero.
 * </p>
 * <p>
 * The counter can be incremented or decremented using {@link #increment()} and {@link
 * #decrement()}, respectively.
 * </p>
 */
public class CounterWithCallbackOnZero {

    private final Runnable callbackOnZero;
    private final Runnable callbackOnChangeFromZero;
    private long counter = 0;


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