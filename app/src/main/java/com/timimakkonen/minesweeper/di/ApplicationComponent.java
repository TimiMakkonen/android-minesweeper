package com.timimakkonen.minesweeper.di;

import com.timimakkonen.minesweeper.GameFragment;
import dagger.Component;

@ApplicationScope
@Component(modules = MinesweeperModelModule.class)
public interface ApplicationComponent {

    void inject(GameFragment gameFragment);
}
