package com.timimakkonen.minesweeper;

import java.util.HashMap;
import java.util.Map;

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

    int value;

    VisualMinesweeperCell(int value) {
        this.value = value;
    }

    private static final Map<Integer, VisualMinesweeperCell> integerToVisualMinesweeperCellMap = new HashMap<>();

    static {
        for (VisualMinesweeperCell cellEnum : VisualMinesweeperCell.values()) {
            integerToVisualMinesweeperCellMap.put(cellEnum.value, cellEnum);
        }
    }

    public static VisualMinesweeperCell newVisualMinesweeperCell(Integer i) {
        return integerToVisualMinesweeperCellMap.get(i);
    }
}
