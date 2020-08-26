package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//Projects own classes.
import java.util.Objects;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.RaceGridViewAdapter;

//TODO Java doc this
public class ActiveRacerDisplayFragment extends Fragment implements CheckpointGrabber, SelectionManagerGrabber {
    private SelectionsStateManager selectionsStateManager;
    private DisplayFilterManager displayFilterManager;
    private Checkpoints checkpoints;

    //TODO Java doc this
    //TODO Add actions for savedInstanceState != null
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setSelectionsStateManager(grabSelectionManager());
        setDisplayFilterManager(grabDisplayFilterManager());
        setCheckpoints(grabCheckpoints());
    }

    //TODO Java doc this
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_racers_display_fragment, container, false);
        view.setElevation(0); //doesn't work when set in XML - I'm unsure why, but I should fix this later when I know.

        final GridView gridView = view.findViewById(R.id.gridView);
        final RaceGridViewAdapter raceGridViewAdapter = new RaceGridViewAdapter(gridView.getContext(), getSelectionsStateManager(), getDisplayFilterManager());
        gridView.setAdapter(raceGridViewAdapter);
        return view;
    }

    //TODO Java doc this
    public SelectionsStateManager getSelectionsStateManager() {
        return selectionsStateManager;
    }

    //TODO Java doc this
    public void setSelectionsStateManager(SelectionsStateManager selectionsStateManager) {
        this.selectionsStateManager = selectionsStateManager;
    }

    //TODO Java doc this
    public Checkpoints getCheckpoints() {
        return checkpoints;
    }

    //TODO Java doc this
    public void setCheckpoints(Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
    }

    //TODO Java doc this
    public DisplayFilterManager getDisplayFilterManager() {
        return displayFilterManager;
    }

    //TODO Java doc this
    public void setDisplayFilterManager(DisplayFilterManager displayFilterManager) {
        this.displayFilterManager = displayFilterManager;
    }

    //TODO Java doc this
    @Override
    public Checkpoints grabCheckpoints() {
        return ((ActiveRacerFragment) Objects.requireNonNull(getParentFragment())).grabCheckpoints();
    }

    //TODO Java doc this
    @Override
    public SelectionsStateManager grabSelectionManager() {
        //return ((SectionsPagerAdapter) (((ViewPager) (getActivity()).findViewById(R.id.mainViewPager))).getAdapter()).getSelectionsStateManager();
         return ((ActiveRacerFragment) Objects.requireNonNull(getParentFragment())).getSelectionsStateManager();
    }

    //TODO Java doc this
    public DisplayFilterManager grabDisplayFilterManager() {
        //return ((SectionsPagerAdapter) (((ViewPager) (getActivity()).findViewById(R.id.mainViewPager))).getAdapter());

        //(ActiveRacerFragment) getActivity().getFragmentManager().findFragmentById(R.id.get)
        //return ((ActiveRacerFragment) getActivity().getFragmentManager().findFragmentByTag("ActiveRacerFragment")).getDisplayFilterManager();
        //MainActivity activity = (MainActivity) getActivity();
        return ((ActiveRacerFragment) Objects.requireNonNull(getParentFragment())).getDisplayFilterManager();
    }

}
