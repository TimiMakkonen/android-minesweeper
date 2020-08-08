
#include <sstream>   // std::ostringstream
#include <stdexcept> // std::invalid_argument
#include <string>    // std::string
#include <vector>    // std::vector

#include <android-minesweeper/game.h>
#include <minesweeper/game.h>

// TODO(Timi): Handle throws?
namespace android_minesweeper {

AndroidMinesweeperGame::AndroidMinesweeperGame()
    : _minesweeperGame{minesweeper::Game(0, 0, &(this->_minesweeperRandom))} {}

AndroidMinesweeperGame::AndroidMinesweeperGame(int gridHeight, int gridWidth, int numOfMines)
    : _minesweeperGame{minesweeper::Game(gridHeight, gridWidth, numOfMines, &(this->_minesweeperRandom))} {}

void AndroidMinesweeperGame::reset(bool keepCreatedMines) { this->_minesweeperGame.reset(keepCreatedMines); }

void AndroidMinesweeperGame::newGame(int gridHeight, int gridWidth, int numOfMines) {
    this->_minesweeperGame.newGame(gridHeight, gridWidth, numOfMines);
}

void AndroidMinesweeperGame::newGame(int gridHeight, int gridWidth, double proportionOfMines) {
    this->_minesweeperGame.newGame(gridHeight, gridWidth, proportionOfMines);
}

void AndroidMinesweeperGame::checkInputCoordinates(int x, int y) { this->_minesweeperGame.checkInputCoordinates(x, y); }

void AndroidMinesweeperGame::markInputCoordinates(int x, int y) {
    try {
        this->_minesweeperGame.markInputCoordinates(x, y);
    } catch (std::invalid_argument& ex) {
        // ignore mark input if the grid has not been initialised
    }
}

void AndroidMinesweeperGame::completeAroundInputCoordinates(int x, int y) {
    this->_minesweeperGame.completeAroundInputCoordinates(x, y);
}

bool AndroidMinesweeperGame::playerHasWon() const { return this->_minesweeperGame.playerHasWon(); }

bool AndroidMinesweeperGame::playerHasLost() const { return this->_minesweeperGame.playerHasLost(); }

bool AndroidMinesweeperGame::isCellVisible(int x, int y) const { return this->_minesweeperGame.isCellVisible(x, y); }

int AndroidMinesweeperGame::getGridHeight() const { return this->_minesweeperGame.getGridHeight(); }

int AndroidMinesweeperGame::getGridWidth() const { return this->_minesweeperGame.getGridWidth(); }

int AndroidMinesweeperGame::getNumOfMines() const { return this->_minesweeperGame.getNumOfMines(); }

std::vector<int> AndroidMinesweeperGame::visualise() const {
    return this->_minesweeperGame.visualise<std::vector<int>>();
}

std::vector<int> AndroidMinesweeperGame::visualiseSolution() const {
    return this->_minesweeperGame.visualiseSolution<std::vector<int>>();
}

std::string AndroidMinesweeperGame::serialise() const {
    std::ostringstream oss;
    this->_minesweeperGame.serialise(oss);
    return oss.str();
}

bool AndroidMinesweeperGame::deserialise(const std::string& inStr) {

    std::istringstream iss(inStr);
    try {
        this->_minesweeperGame.deserialise(iss);
    } catch (std::invalid_argument& ex) {
        // return false if unsuccessful
        return false;
    }
    // return true if successful
    return true;
}

// static
int AndroidMinesweeperGame::maxNumOfMines(int gridHeight, int gridWidth) {
    return minesweeper::Game::maxNumOfMines(gridHeight, gridWidth);
}

// static
double AndroidMinesweeperGame::maxProportionOfMines(int gridHeight, int gridWidth) {
    return minesweeper::Game::maxProportionOfMines(gridHeight, gridWidth);
}

// static
int AndroidMinesweeperGame::minNumOfMines() { return minesweeper::Game::minNumOfMines(); }

// static
double AndroidMinesweeperGame::minProportionOfMines() { return minesweeper::Game::minProportionOfMines(); }

} // namespace android_minesweeper
