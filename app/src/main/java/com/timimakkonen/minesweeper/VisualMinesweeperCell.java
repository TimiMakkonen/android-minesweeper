package com.timimakkonen.minesweeper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This enum class represent the visual state of a cell, allowing views/ui to display a cell.
 * </p>
 * <p>
 * An instance of 'VisualMinesweeperCell' can be obtained using static 'newVisualMinesweeperCell'
 * method, if needed.
 * </p>
 */
public enum VisualMinesweeperCell {
    UNCHECKED(-1),
    EMPTY(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    MINE(9), // not a typo! :D
    MARKED(10);

    private static final Map<Integer, VisualMinesweeperCell> integerToVisualMinesweeperCellMap =
            new HashMap<>();

    static {
        for (VisualMinesweeperCell cellEnum : VisualMinesweeperCell.values()) {
            integerToVisualMinesweeperCellMap.put(cellEnum.value, cellEnum);
        }
    }

    int value;

    VisualMinesweeperCell(int value) {
        this.value = value;
    }

    public static VisualMinesweeperCell newVisualMinesweeperCell(Integer i) {
        return integerToVisualMinesweeperCellMap.get(i);
    }
}
