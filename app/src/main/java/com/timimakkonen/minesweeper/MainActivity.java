package com.timimakkonen.minesweeper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.inject.Inject;

/**
 * <p>
 * This 'MainActivity' class is the first visual/ui point of contact for this app. It is responsible
 * for hosting the content fragments in the 'content_main.xml' layout and initialising the toolbar
 * and the navigation controller.
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    private static final String SAVE_WAS_CORRUPTED_KEY = "save_was_corrupted";

    AppBarConfiguration appBarConfiguration;

    @Inject
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting up content view
        setContentView(R.layout.activity_main);

        // setting up application toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setting up action bar with navigation controller
        // Odd workaround to allow using 'androidx.fragment.app.FragmentContainerView' instead of 'fragment' as navigation host.
        // https://issuetracker.google.com/issues/142847973
        final NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(
                        R.id.nav_host_fragment);
        assert navHostFragment != null;
        final NavController navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // injecting dependencies
        ((MinesweeperApplication) getApplicationContext()).appComponent.inject(this);

        // displaying alert dialog if save file was corrupted
        if (localStorage.getBoolean(SAVE_WAS_CORRUPTED_KEY, false)) {
            showCorruptedGameSaveDialog();
            localStorage.setBoolean(SAVE_WAS_CORRUPTED_KEY, false);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
               || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(
                R.id.action_global_aboutFragment);
    }

    private void showCorruptedGameSaveDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.corrupted_game_save_dialog_title)
                .setMessage(R.string.corrupted_game_save_dialog_message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
