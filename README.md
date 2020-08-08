# Minesweeper game on Android

[![GitHub License](https://img.shields.io/github/license/TimiMakkonen/android-minesweeper)](/LICENSE)
![GitHub Latest Release Tag](https://img.shields.io/github/v/tag/TimiMakkonen/android-minesweeper)

Android minesweeper game utilising [Minesweeper game library](https://github.com/TimiMakkonen/minesweeper) version 8.5.1.

## Table of contents

* [How to clone](#how-to-clone)
* [How to use](#how-to-use)
* [External libraries](#external-libraries)
* [Tools used](#tools-used)
* [Version history](#version-history)
* [Screenshots](#screenshots)
* [Fixes and features left to consider/implement](#fixes-and-features-left-to-considerimplement)

## How to clone

If you want to clone this git repository, use

```console
git clone --recurse-submodules https://github.com/TimiMakkonen/android-minesweeper.git
```

or something similar to ensure that ['TimiMakkonen/minesweeper'](https://github.com/TimiMakkonen/minesweeper) submodule and its external submodules get cloned properly.

## How to use

Build, run and install as expected in ['Android Studio'](https://developer.android.com/studio) or any other environment of your choosing.

This game uses native C++ code to utilise ['Minesweeper game library'](https://github.com/TimiMakkonen/minesweeper), so you need to have ['Android NDK'](https://developer.android.com/ndk) enabled.

Modifying the Java/Android portion of the code should work as expected, but if you want to modify the C++ portion of the code, you should have ['SWIG'](http://www.swig.org) installed. If you make any changes on the C++ side, you should follow the instructions found in ['SWIG_INSTRUCTIONS.txt'](/app/src/main/cpp/SWIG_INSTRUCTIONS.txt) to rerun ['SWIG'](http://www.swig.org) so that it can modify JNI wrapping if needed.

## External libraries

* [TimiMakkonen/minesweeper](https://github.com/TimiMakkonen/minesweeper) (v8.5.1) for minesweeper game logic in C++

## Tools used

* [SWIG](http://www.swig.org) to create JNI wrapper around C++ interface code

## Version history

### Version DEVELOP

* Added 'SolutionFragment' to show the solution of the current grid.
* Added primary/secondary click-action-switch button.
* Added 'complete-around'-feature.
  * If you perform a click action on a visible cell that has: 'number of
    marked neighbouring cells' == 'number of mines around', then all
    unchecked/invisible cells around this cell get automatically
    checked, since they trivially should not have a mine.
  * This action can result in a loss if a cell that does not have a mine
    has been erroneously marked.
* Modified 'maxGridHeight' and 'maxGridWidth' methods in
  'MinesweeperGridView' to make sure that cells are at least 24dp.
* Handle the exception (by ignoring) when trying to mark a cell in an
  uninitialised grid.
* Added preliminary Javadoc comments to classes.
* In 'MaterialIntSliderAndEditText':
  * Added attributes.
  * Added 'setValue', 'removeOnChangeListener' and
    'clearOnChangeListeners' methods.
  * Fixed typos in 'setMinValue' and 'setMaxValue' methods.
* Updated [TimiMakkonen/minesweeper](https://github.com/TimiMakkonen/minesweeper) submodule to v8.5.1

### Version 0.2.0

* Added saving functionality and made the game save on exit.
  * Also added an option to switch off this functionality in settings.
  * Potential save corruptions have also been handled properly.
* Added about page.
* Added win/loss background color changes/tints.
* Made dark theme selection and setting to work properly.
* Added LocalStorage class to handle all local storage related tasks.
  * SettingsFragment is the only exception to this.
    (It handles this automatically by itself.)
* Annotated various ints with '@ColorInt', as appropriate.
* Fixed various warnings.
* Deleted unnecessary relic pieces of code.

### Version 0.1.0

* Basic working minesweeper game in mostly playable form.
* Currently you can play: Easy, Medium, Hard or Custom (up to 30x20) games.
* Preliminary dark theme (night mode) support.
* Progress is currently not saved on exit.

## Screenshots

Gameplay example | Gameplay example (larger grid) | Gameplay example (dark theme) | Custom game selection dialog
-----------------|--------------------------------|-------------------------------|-----------------------------
[![Gameplay example](/screenshots/GamePlay1.png "Gameplay example")](/screenshots/GamePlay1.png) | [![Gameplay example (larger grid)](/screenshots/GamePlay2.png "Gameplay example (larger grid)")](/screenshots/GamePlay2.png) | [![Gameplay example (dark theme)](/screenshots/DarkTheme_GamePlay.png "Gameplay example (dark theme)")](/screenshots/DarkTheme_GamePlay.png) | [![Custom game selection dialog](/screenshots/CustomGameSelection.png "Custom game selection dialog")](/screenshots/CustomGameSelection.png)

## Fixes and features left to consider/implement

* Add tests.
* Handle edge cases and invalid arguments of various method parameters.
* Consider automatising ['SWIG'](http://www.swig.org).
  * Would be nice, but would require anyone who builds this app to have ['SWIG'](http://www.swig.org) installed.
* Consider handling exceptions in C++ code.
* Add more customisation options (more colors, minesweeper symbols, etc.).
* Clean up and comment code.
* Add help page.
* Add zooming option to grid.
* Add 'peek solution' option which instead of showing solution in
  another fragment, shows the solution in place of the current game.
