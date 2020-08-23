package com.timimakkonen.minesweeper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.inject.Inject;

/**
 * <p>
 * This fragment class is the fragment that is responsible for displaying the current minesweeper
 * game and handling the android lifecycle, and other android specific details.
 * </p>
 * <p>
 * This fragment:
 * </p>
 * <ul>
 *     <li>forwards needed data to {@link MinesweeperGridView} and passes its touch events to {@link GameViewModel}
 *         </li>
 *     <li>starts 'save-game'-feature when paused</li>
 *     <li>deals with minesweeper game related menu items, such as 'New Game' and 'Show solution'
 *     </li>
 *     <li>starts dialogs when game has been won, lost, or when a new custom game is requested</li>
 * </ul>
 */
public class GameFragment extends Fragment {

    private static final String TAG = "GameFragment";

    @Inject
    GameViewModel viewModel;
    @Inject
    LocalStorage localStorage;

    private MinesweeperGridView minesweeperView;
    private ConstraintLayout gameFragmentView;
    private MaterialButton primSecoSwitchButton;

    private boolean hasOriginalColorDrawableBackground = false;
    @ColorInt
    private int originalBackgroundColor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        minesweeperView = view.findViewById(R.id.minesweeperGridView);
        primSecoSwitchButton = view.findViewById(R.id.primSecoSwitchButton);

        gameFragmentView = view.findViewById(R.id.game_fragment_view);
        hasOriginalColorDrawableBackground = initBackgroundColorField();

        viewModel.getVisualMinesweeperCells()
                 .observe(getViewLifecycleOwner(), visualMinesweeperCells -> minesweeperView
                         .setVisualMinesweeperCellsAndResize(visualMinesweeperCells)
                 );

        viewModel.hasPlayerWon().observe(getViewLifecycleOwner(), playerHasWon -> {
            if (playerHasWon) {
                onGameWin();
            } else {
                onGameNotWin();
            }
        });

        viewModel.hasPlayerLost().observe(getViewLifecycleOwner(), playerHasLost -> {
            if (playerHasLost) {
                onGameLoss();
            } else {
                onGameNotLoss();
            }
        });

        minesweeperView.addMinesweeperEventListener(
                new MinesweeperGridView.OnMinesweeperGridViewEventListener() {
                    @SuppressWarnings("unused")
                    @Override
                    public void onCellPrimaryAction(int x, int y) {
                        Log.d(TAG, String.format("Primary cell action on (%d, %d)", x, y));
                        viewModel.primaryMinesweeperCoordinatesAction(x, y);
                    }

                    @SuppressWarnings("unused")
                    @Override
                    public void onCellSecondaryAction(int x, int y) {
                        Log.d(TAG, String.format("Secondary cell action on (%d, %d)", x, y));
                        viewModel.secondaryMinesweeperCoordinatesAction(x, y);
                    }
                });

        primSecoSwitchButton.setOnClickListener(v -> viewModel.switchMinesweeperPrimSecoActions());

        viewModel.isPrimaryActionCheck().observe(getViewLifecycleOwner(), primaryActionIsCheck -> {
            if (primaryActionIsCheck) {
                primSecoSwitchButton.setIconResource(
                        R.drawable.ic_visibility_with_marked_symbol_black_24dp);
            } else {
                primSecoSwitchButton.setIconResource(
                        R.drawable.ic_marked_symbol_with_visibility_black_24dp);
            }

        });
    }

    private boolean initBackgroundColorField() {
        final Drawable originalBackground = gameFragmentView.getBackground();
        if (originalBackground != null) {
            if (originalBackground instanceof ColorDrawable) {
                originalBackgroundColor = ((ColorDrawable) originalBackground).getColor();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        ((MinesweeperApplication) requireActivity().getApplicationContext())
                .appComponent
                .inject(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();

        // primary-secondary click action switch setup
        if (localStorage.getUsePrimSecoSwitchKey(true)) {
            primSecoSwitchButton.setVisibility(View.VISIBLE);
            ConstraintLayout.LayoutParams params =
                    (ConstraintLayout.LayoutParams) primSecoSwitchButton.getLayoutParams();
            String chosenButtonBias = localStorage.getPrimSecoSwitchHorizBias("start");
            switch (chosenButtonBias) {
                case "start":
                    params.horizontalBias = 0f;
                    break;
                case "center":
                    params.horizontalBias = 0.5f;
                    break;
                case "end":
                    params.horizontalBias = 1f;
                    break;
                case "custom":
                    params.horizontalBias = localStorage.getPrimSecoSwitchCustomHorizBias(0) / 100f;
                    break;
            }
            primSecoSwitchButton.setLayoutParams(params);
        } else {
            primSecoSwitchButton.setVisibility(View.GONE);
            viewModel.setPrimaryActionIsCheckToDefault();
        }
    }

    @Override
    public void onPause() {
        if (localStorage.getSaveAndResume(true)) {
            viewModel.save();
        }
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                showSettings();
                return true;
            case R.id.action_restart_without_mines:
                viewModel.restartWithoutMines();
                return true;
            case R.id.action_restart_with_mines:
                viewModel.restartWithMines();
                return true;
            case R.id.action_show_solution:
                showSolution();
                return true;
            case R.id.action_new_game_easy:
                viewModel.startNewEasyGame();
                return true;
            case R.id.action_new_game_medium:
                viewModel.startNewMediumGame();
                return true;
            case R.id.action_new_game_hard:
                viewModel.startNewHardGame();
                return true;
            case R.id.action_new_game_clickable_custom:
                customNewGameDialog(minesweeperView.clickableMaxGridHeight(),
                                    minesweeperView.clickableMaxGridWidth());
                return true;
            case R.id.action_new_game_reasonable_custom:
                customNewGameDialog(minesweeperView.reasonableMaxGridHeight(),
                                    minesweeperView.reasonableMaxGridWidth());
                return true;
            case R.id.action_new_game_unreasonable_custom:
                customNewGameDialog(minesweeperView.unreasonableMaxGridHeight(),
                                    minesweeperView.unreasonableMaxGridWidth());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void customNewGameDialog(int maxGridHeight, int maxGridWidth) {

        final MaterialAlertDialogBuilder newGameAlert = new MaterialAlertDialogBuilder(
                requireActivity());

        newGameAlert.setTitle(R.string.new_game_dialog_title);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_custom_grid, null);
        newGameAlert.setView(dialogView);


        final MaterialIntSliderAndEditText gridHeightSliderWithEditText = dialogView.findViewById(
                R.id.sliderandedittext_grid_height);
        gridHeightSliderWithEditText.setMinValue(0);
        gridHeightSliderWithEditText.setMaxValue(maxGridHeight);

        final MaterialIntSliderAndEditText gridWidthSliderWithEditText = dialogView.findViewById(
                R.id.sliderandedittext_grid_width);
        gridWidthSliderWithEditText.setMinValue(0);
        gridWidthSliderWithEditText.setMaxValue(maxGridWidth);

        final MaterialIntSliderAndEditText numOfMinesSliderWithEditText = dialogView.findViewById(
                R.id.sliderandedittext_grid_num_of_mines);
        numOfMinesSliderWithEditText.setMinValue(0);
        numOfMinesSliderWithEditText.setMaxValue(0);

        gridHeightSliderWithEditText.addOnChangeListener(
                (intSliderAndEditText, value) -> numOfMinesSliderWithEditText.setMaxValue(
                        viewModel.maxNumOfMines(gridHeightSliderWithEditText.getValue(),
                                                gridWidthSliderWithEditText.getValue())));

        gridWidthSliderWithEditText.addOnChangeListener(
                (intSliderAndEditText, value) -> numOfMinesSliderWithEditText.setMaxValue(
                        viewModel.maxNumOfMines(gridHeightSliderWithEditText.getValue(),
                                                gridWidthSliderWithEditText.getValue())));


        newGameAlert.setNeutralButton(R.string.cancel, (dialog, which) -> {
        });

        newGameAlert.setPositiveButton(
                R.string.accept,
                (dialog, which) -> viewModel.startNewGame(gridHeightSliderWithEditText.getValue(),
                                                          gridWidthSliderWithEditText.getValue(),
                                                          numOfMinesSliderWithEditText.getValue()));

        newGameAlert.show();
    }


    private void showSettings() {

        NavHostFragment.findNavController(GameFragment.this)
                       .navigate(R.id.action_gameFragment_to_settingsFragment);
    }

    private void showSolution() {

        NavHostFragment.findNavController(GameFragment.this)
                       .navigate(R.id.action_gameFragment_to_solutionFragment);
    }

    private void onGameWin() {
        resetBackgroundToOriginalColorDrawable();
        blendBackgroundColorDrawableWith(Color.GREEN);
        showWinAlert();
    }

    private void onGameNotWin() {
        resetBackgroundToOriginalColorDrawable();
    }

    private void onGameLoss() {
        resetBackgroundToOriginalColorDrawable();
        blendBackgroundColorDrawableWith(Color.RED);
        showLossAlert();
    }

    private void onGameNotLoss() {
        resetBackgroundToOriginalColorDrawable();
    }

    private void resetBackgroundToOriginalColorDrawable() {
        if (hasOriginalColorDrawableBackground) {
            gameFragmentView.setBackgroundColor(originalBackgroundColor);
        }
    }

    private void blendBackgroundColorDrawableWith(@ColorInt int color) {
        final Drawable currentBackground = gameFragmentView.getBackground();
        if (currentBackground != null) {
            if (currentBackground instanceof ColorDrawable) {
                int currentColor = ((ColorDrawable) currentBackground).getColor();
                gameFragmentView.setBackgroundColor(
                        ColorUtils.blendARGB(currentColor, color, 0.2f));
            }
        }
    }

    private void showWinAlert() {
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.win_alert_title)
                .setSingleChoiceItems(R.array.won_game_dialog_options, 0, null)
                .setNeutralButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    int selectedItem =
                            ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    Log.d(TAG, String.format("showWinAlert: onClick: Confirmed option number: %d",
                                             selectedItem));
                    String choice = getResources().getStringArray(
                            R.array.won_game_dialog_options)[selectedItem];

                    if (choice.equals(getString(R.string.play_again))) {
                        viewModel.restartWithoutMines();
                    } else if (choice.equals(getString(R.string.play_easy_game))) {
                        viewModel.startNewEasyGame();
                    } else if (choice.equals(getString(R.string.play_medium_game))) {
                        viewModel.startNewMediumGame();
                    } else if (choice.equals(getString(R.string.play_hard_game))) {
                        viewModel.startNewHardGame();
                    } else if (choice.equals(getString(R.string.play_clickable_custom_game))) {
                        customNewGameDialog(minesweeperView.clickableMaxGridHeight(),
                                            minesweeperView.clickableMaxGridWidth());
                    } else if (choice.equals(getString(R.string.play_reasonable_custom_game))) {
                        customNewGameDialog(minesweeperView.reasonableMaxGridHeight(),
                                            minesweeperView.reasonableMaxGridWidth());
                    } else if (choice.equals(getString(R.string.play_unreasonable_custom_game))) {
                        customNewGameDialog(minesweeperView.unreasonableMaxGridHeight(),
                                            minesweeperView.unreasonableMaxGridWidth());
                    }
                })
                .show();
    }

    private void showLossAlert() {
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.lost_alert_title)
                .setSingleChoiceItems(R.array.lost_game_dialog_options, 0, null)
                .setNeutralButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    int selectedItem =
                            ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    Log.d(TAG, String.format("showLostAlert: onClick: Confirmed option number: %d",
                                             selectedItem));
                    String choice = getResources().getStringArray(
                            R.array.lost_game_dialog_options)[selectedItem];

                    if (choice.equals(getString(R.string.play_again))) {
                        viewModel.restartWithoutMines();
                    } else if (choice.equals(getString(R.string.play_easy_game))) {
                        viewModel.startNewEasyGame();
                    } else if (choice.equals(getString(R.string.play_medium_game))) {
                        viewModel.startNewMediumGame();
                    } else if (choice.equals(getString(R.string.play_hard_game))) {
                        viewModel.startNewHardGame();
                    } else if (choice.equals(getString(R.string.play_clickable_custom_game))) {
                        customNewGameDialog(minesweeperView.clickableMaxGridHeight(),
                                            minesweeperView.clickableMaxGridWidth());
                    } else if (choice.equals(getString(R.string.play_reasonable_custom_game))) {
                        customNewGameDialog(minesweeperView.reasonableMaxGridHeight(),
                                            minesweeperView.reasonableMaxGridWidth());
                    } else if (choice.equals(getString(R.string.play_unreasonable_custom_game))) {
                        customNewGameDialog(minesweeperView.unreasonableMaxGridHeight(),
                                            minesweeperView.unreasonableMaxGridWidth());
                    }
                })
                .show();
    }
}
