package com.timimakkonen.minesweeper.di;

import com.timimakkonen.minesweeper.GameFragment;
import com.timimakkonen.minesweeper.MainActivity;
import com.timimakkonen.minesweeper.MinesweeperApplication;
import com.timimakkonen.minesweeper.SettingsFragment;

import dagger.BindsInstance;
import dagger.Component;

@ApplicationScope
@Component(modules = {MinesweeperModelModule.class, ApplicationModule.class})
public interface ApplicationComponent {

    void inject(GameFragment gameFragment);

    void inject(MinesweeperApplication minesweeperApplication);

    void inject(SettingsFragment settingsFragment);

    void inject(MainActivity mainActivity);

    @Component.Factory
    interface Factory {
        ApplicationComponent create(@BindsInstance MinesweeperApplication minesweeperApplication);
    }
}
