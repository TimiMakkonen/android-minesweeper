# Minesweeper game on Android

[![GitHub License](https://img.shields.io/github/license/TimiMakkonen/android-minesweeper)](/LICENSE)
![GitHub Latest Release Tag](https://img.shields.io/github/v/tag/TimiMakkonen/android-minesweeper)

Android minesweeper game utilising [Minesweeper game library](https://github.com/TimiMakkonen/minesweeper) version 8.5.2.

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

* [TimiMakkonen/minesweeper](https://github.com/TimiMakkonen/minesweeper) (v8.5.2) for minesweeper game logic in C++

## Tools used

* [SWIG](http://www.swig.org) to create JNI wrapper around C++ interface code

## Version history

### Version DEVELOP

* Updated some Android dependencies.

### Version 0.4.0

* Added zooming feature.
  * Zooming and panning/scrolling works as expected.
* Added 'reasonable' and 'unreasonable custom game' options.
  * Now:
    * 'custom game' should work well even without zooming,
    * 'reasonable custom game' should work well zoomed, and
    * 'unreasonable custom game' is just ridiculous.
  * Note that for very large grids saving and loading a game slows down
    considerably and the save file size bloats.
* Updated 'mine' and 'one'-symbols.
* Moved 'SharedPreference'-keys to 'LocalStorage' and created explicit
  setters and getters to access their data.
  * 'LocalStorage' and 'SettingsFragment' are now the only classes which
    access 'SharedPreferences' directly.
* Updated Javadoc.
* Re-enabled 'unused'-warning and fixed or suppressed them.
* Changed 'MinesweeperGridView' to allow multiple
  'MinesweeperGridViewEventListener's.
* Updated [TimiMakkonen/minesweeper](https://github.com/TimiMakkonen/minesweeper) submodule to 'v8.5.2'.


### Version 0.3.0

* Added 'SolutionFragment' to show the solution of the current grid.
* Added primary/secondary click-action-switch button.
  * Can be moved horizontally or hidden in 'Settings'.
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
* Added some new icons and slightly modified old ones.
* Bumped 'minSdkVersion' from 19 to 21.
* Updated [TimiMakkonen/minesweeper](https://github.com/TimiMakkonen/minesweeper) submodule to 'v8.5.1'.

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

Gameplay example | Solution example | Gameplay example (dark theme) | Custom game selection dialog | Settings page
-----------------|------------------|-------------------------------|------------------------------|--------------
[![Gameplay example](/screenshots/v0.3.0/GamePlay_v0.3.0.png "Gameplay example")](/screenshots/v0.3.0/GamePlay_v0.3.0.png) | [![Solution example](/screenshots/v0.3.0/Solution_v0.3.0.png "Solution example")](/screenshots/v0.3.0/Solution_v0.3.0.png) | [![Gameplay example (dark theme)](/screenshots/v0.3.0/GamePlay_DarkTheme_v0.3.0.png "Gameplay example (dark theme)")](/screenshots/v0.3.0/GamePlay_DarkTheme_v0.3.0.png) | [![Custom game selection dialog](/screenshots/v0.1.0/CustomGameSelection_v0.1.0.png "Custom game selection dialog")](/screenshots/v0.1.0/CustomGameSelection_v0.1.0.png) | [![Settings page](/screenshots/v0.3.0/Settings_v0.3.0.png "Settings page")](/screenshots/v0.3.0/Settings_v0.3.0.png)

Large gameplay example | Large solution example | Large gameplay example (dark theme) | Zoomed-in gameplay example | Zoomed-in gameplay example (dark theme)
-----------------------|------------------------|-------------------------------------|----------------------------|----------------------------------------
[![Large gameplay example](/screenshots/v0.4.0/GamePlay_LargeGame_v0.4.0.png "Large gameplay example")](/screenshots/v0.4.0/GamePlay_LargeGame_v0.4.0.png) | [![Large solution example](/screenshots/v0.4.0/Solution_LargeGame_v0.4.0.png "Large solution example")](/screenshots/v0.4.0/Solution_LargeGame_v0.4.0.png) | [![Large gameplay example (dark theme)](/screenshots/v0.4.0/GamePlay_DarkTheme_LargeGame_v0.4.0.png "Large gameplay example (dark theme)")](/screenshots/v0.4.0/GamePlay_DarkTheme_LargeGame_v0.4.0.png) | [![Zoomed-in gameplay example](/screenshots/v0.4.0/ZoomedIn_GamePlay_LargeGame_v0.4.0.png "Zoomed-in gameplay example")](/screenshots/v0.4.0/ZoomedIn_GamePlay_LargeGame_v0.4.0.png) | [![Zoomed-in gameplay example (dark theme)](/screenshots/v0.4.0/ZoomedIn_GamePlay_DarkTheme_LargeGame_v0.4.0.png "Zoomed-in gameplay example (dark theme)")](/screenshots/v0.4.0/ZoomedIn_GamePlay_DarkTheme_LargeGame_v0.4.0.png)

## Fixes and features left to consider/implement

* Add tests.
* Handle edge cases and invalid arguments of various method parameters.
* Consider automatising ['SWIG'](http://www.swig.org).
  * Would be nice, but would require anyone who builds this app to have ['SWIG'](http://www.swig.org) installed.
* Consider handling exceptions in C++ code.
* Add more customisation options (more colors, minesweeper symbols, etc.).
* Clean up and comment code.
* Add help page.
* Add 'peek solution' option which instead of showing solution in
  another fragment, shows the solution in place of the current game.
* Consider removing 'primaryActionIsCheck' LiveData in 'GameViewModel'.
