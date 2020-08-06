package com.timimakkonen.minesweeper;

/**
 * <p>
 * This class is responsible for encapsulating the minesweeper data that is needed by game
 * views/ui.
 * </p>
 */
public class MinesweeperDataForView {

    private final VisualMinesweeperCell[][] currentVisualMinesweeperCells;
    private final boolean playerHasWon;
    private final boolean playerHasLost;

    public MinesweeperDataForView(VisualMinesweeperCell[][] currentVisualMinesweeperCells,
                                  boolean playerHasWon, boolean playerHasLost) {
        this.currentVisualMinesweeperCells = currentVisualMinesweeperCells;
        this.playerHasWon = playerHasWon;
        this.playerHasLost = playerHasLost;
    }

    // getters:

    public VisualMinesweeperCell[][] getCurrentVisualMinesweeperCells() {
        return currentVisualMinesweeperCells;
    }

    public boolean hasPlayerWon() {
        return playerHasWon;
    }

    public boolean hasPlayerLost() {
        return playerHasLost;
    }

}
