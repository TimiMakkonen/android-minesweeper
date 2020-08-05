package com.timimakkonen.minesweeper.di;

import com.timimakkonen.minesweeper.GameFragment;
import com.timimakkonen.minesweeper.MainActivity;
import com.timimakkonen.minesweeper.MinesweeperApplication;
import com.timimakkonen.minesweeper.SettingsFragment;
import com.timimakkonen.minesweeper.SolutionFragment;

import dagger.BindsInstance;
import dagger.Component;

@ApplicationScope
@Component(modules = {MinesweeperModelModule.class, ApplicationModule.class})
public interface ApplicationComponent {

    void inject(MinesweeperApplication minesweeperApplication);

    void inject(MainActivity mainActivity);

    void inject(GameFragment gameFragment);

    void inject(SolutionFragment solutionFragment);

    void inject(SettingsFragment settingsFragment);

    @Component.Factory
    interface Factory {
        ApplicationComponent create(@BindsInstance MinesweeperApplication minesweeperApplication);
    }
}
