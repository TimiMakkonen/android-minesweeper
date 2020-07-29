
%module minesweeper_game
%{
#include <android-minesweeper/game.h>
%}

%include "std_vector.i"
%include "std_string.i"

namespace std {
  %template(vector_int) vector<int>;
}

%include "../src/android-minesweeper/game.h"
