#ifndef ANDROID_MINESWEEPER_GAME_H
#define ANDROID_MINESWEEPER_GAME_H

#include <vector> // std::vector

#include <minesweeper/game.h>
#include <minesweeper/random.h>

namespace android_minesweeper {

class AndroidMinesweeperGame {
  private:
    // +---------+
    // | fields: |
    // +---------+

    minesweeper::Game _minesweeperGame;
    minesweeper::Random _minesweeperRandom;

  public:
    // +-----------------+
    // | public methods: |
    // +-----------------+

    // constructors:
    AndroidMinesweeperGame();
    AndroidMinesweeperGame(int gridHeight, int gridWidth, int numOfMines);

    // reset and new game methods:
    void reset(bool keepCreatedMines);
    void newGame(int gridHeight, int gridWidth, int numOfMines);
    void newGame(int gridHeight, int gridWidth, double proportionOfMines);

    // to check user given coordinates, and make it visible
    void checkInputCoordinates(int x, int y);

    // to mark (or unmark) given coordinates
    void markInputCoordinates(int x, int y);

    // game progress information:
    bool playerHasWon() const;
    bool playerHasLost() const;

    // getters:
    int getGridHeight() const;
    int getGridWidth() const;
    int getNumOfMines() const;

    // get visual information of each cell:
    std::vector<int> visualise() const;

    // +------------------------+
    // | public static methods: |
    // +------------------------+

    // static maximum number/proportion of mines checking methods:
    static int maxNumOfMines(int gridHeight, int gridWidth);
    static double maxProportionOfMines(int gridHeight, int gridWidth);

    // static minimum number/proportion of mines checking methods:
    static int minNumOfMines();
    static double minProportionOfMines();
};
} // namespace android_minesweeper

#endif // ANDROID_MINESWEEPER_GAME_H