package com.timimakkonen.minesweeper;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

/**
 * <p>
 * This fragment is responsible for displaying solution visualisation of minesweeper grid and
 * handling the android lifecycle, and other android specific details related to this.
 * </p>
 */
public class SolutionFragment extends Fragment {

    @Inject
    SolutionViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.solution_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final MinesweeperGridView minesweeperSolutionView = view.findViewById(
                R.id.solutionMinesweeperGridView);

        viewModel.getVisualMinesweeperCells()
                 .observe(getViewLifecycleOwner(),
                          minesweeperSolutionView::setVisualMinesweeperCellsAndResize
                 );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        ((MinesweeperApplication) requireActivity().getApplicationContext())
                .appComponent
                .inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.updateSolutionVisualisation();
    }
}