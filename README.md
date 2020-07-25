# Minesweeper game on Android

[![GitHub License](https://img.shields.io/github/license/TimiMakkonen/android-minesweeper)](/LICENSE)
![GitHub Latest Release Tag](https://img.shields.io/github/v/tag/TimiMakkonen/android-minesweeper)

Android minesweeper game utilising [Minesweeper game library](https://github.com/TimiMakkonen/minesweeper) version 8.4.3.

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

* [TimiMakkonen/minesweeper](https://github.com/astoeckel/json) (v8.4.3) for minesweeper game logic in C++

## Tools used

* [SWIG](http://www.swig.org) to create JNI wrapper around C++ interface code

## Version history

### Version DEVELOP

* Added win/loss background color changes/tints.
* Annotated various ints with '@ColorInt', as appropriate.

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

* Support dark theme (night mode) properly.
* Make app to save the game on exit, and load the game back when returning.
* Add tests.
* Handle edge cases and invalid arguments of various method parameters.
* Automatise ['SWIG'](http://www.swig.org).
* Handle exceptions in C++ code.
* Add more customisation options (more colors, minesweeper symbols, etc.).
* Clean up and comment code.
* Add help and about pages.
* Add zooming option to grid.
* Add solution option/screen.
