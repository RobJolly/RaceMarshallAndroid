package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.RaceGridViewAdapter;

public class ActiveRacerDisplayFragment extends Fragment implements CheckpointGrabber, SelectionManagerGrabber {
    private SelectionsStateManager selectionsStateManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_racers_display_fragment, container, false);
        view.setElevation(0); //doesn't work when set in XML - I'm unsure why, but I should fix this later when I know.

        selectionsStateManager = grabSelectionManager();

        final GridView gridView = ((GridView) view.findViewById(R.id.gridView));
        final RaceGridViewAdapter raceGridViewAdapter = new RaceGridViewAdapter(gridView.getContext(), this);
        gridView.setAdapter(raceGridViewAdapter);
        return view;
    }

  /*  public void setRacers(ArrayList<Integer> passedRacers) {
        this.racers = passedRacers;
    }

    public void passSelected(ArrayList<Integer> selectedList) {
        ((ActiveRacerActionFragment) getFragmentManager().findFragmentById(R.id.actionFragment)).setSelectedRacers(selectedList);
    }

    public void resetSelected() {
        ((RaceGridViewAdapter)(((GridView)(getView().findViewById(R.id.gridView))).getAdapter())).resetSelected();
    }

    public void removeSelectedRacers() {
        ((RaceGridViewAdapter)(((GridView)(getView().findViewById(R.id.gridView))).getAdapter())).removeSelected();
    }*/

    @Override
    public Checkpoints grabCheckpoints() {
        return ((ActiveRacerFragment) getParentFragment()).grabCheckpoints();
    }

    @Override
    public SelectionsStateManager grabSelectionManager() {
        return ((ActiveRacerFragment) getParentFragment()).grabSelectionManager();
    }
}
