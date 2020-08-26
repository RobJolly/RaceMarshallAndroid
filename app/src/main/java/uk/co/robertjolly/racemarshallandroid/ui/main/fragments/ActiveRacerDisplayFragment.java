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

/**
 * This is the active racer display fragment. The job of this fragment is to show to the user,
 * the possible racers to select from, and to change the SelectionsStateManager accordingly.
 */
public class ActiveRacerDisplayFragment extends Fragment implements CheckpointGrabber, SelectionManagerGrabber {
    private SelectionsStateManager selectionsStateManager;
    private DisplayFilterManager displayFilterManager;
    private Checkpoints checkpoints;

    /**
     * This is the onCreate function. This initialises the values - grabs the selection manager, filters and checkpoints.
     * @param savedInstanceState The bundle from which to grab data from a previous instance, if any is there.
     */
    //TODO Add actions for savedInstanceState != null
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setSelectionsStateManager(grabSelectionManager());
        setDisplayFilterManager(grabDisplayFilterManager());
        setCheckpoints(grabCheckpoints());
    }

    /**
     * This is called upon the showing of the view. In this, the grid is initialised with the data to be shown
     * @param inflater The inflater
     * @param container The container
     * @param savedInstanceState The bundle from which to grab data from a previous instance, if any is there
     * @return The view with data set/shown
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_racers_display_fragment, container, false);
        //view.setElevation(0); //doesn't work when set in XML - I'm unsure why, but I should fix this later when I know.

        //creates the grid view to be shown in this fragment
        final GridView gridView = view.findViewById(R.id.gridView);
        final RaceGridViewAdapter raceGridViewAdapter = new RaceGridViewAdapter(gridView.getContext(), getSelectionsStateManager(), getDisplayFilterManager());
        gridView.setAdapter(raceGridViewAdapter);
        return view;
    }

    /**
     * This gets the SelectionStateManager stored within this fragment
     * @return The SelectionStateManager stored within this fragment
     */
    public SelectionsStateManager getSelectionsStateManager() {
        return selectionsStateManager;
    }

    /**
     * This sets the SelectionStateManager stored within this fragment
     * @param selectionsStateManager The SelectionStateManager to store
     */
    public void setSelectionsStateManager(SelectionsStateManager selectionsStateManager) {
        this.selectionsStateManager = selectionsStateManager;
    }

    /**
     * Gets the checkpoints stored within this fragment
     * @return the checkpoints stored within this fragment
     */
    public Checkpoints getCheckpoints() {
        return checkpoints;
    }

    /**
     * Sets the checkpoints stored within this fragment
     * @param checkpoints  the checkpoints to be stored within this fragment
     */
    public void setCheckpoints(Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
    }

    /**
     * Gets the displayFilterManager stored within this fragment
     * @return the DisplayFilterManager stored within this fragment
     */
    public DisplayFilterManager getDisplayFilterManager() {
        return displayFilterManager;
    }

    /**
     * Sets the DisplayFilterManager stored within this fragment
     * @param displayFilterManager the DisplayFilterManager to be stored within this fragment
     */
    public void setDisplayFilterManager(DisplayFilterManager displayFilterManager) {
        this.displayFilterManager = displayFilterManager;
    }

    /**
     * This grabs the checkpoints, stored within the parent fragment (ActiveRacerFragment)
     * @return Checkpoints, grabbed from the parent fragment.
     */
    @Override
    public Checkpoints grabCheckpoints() {
        return ((ActiveRacerFragment) Objects.requireNonNull(getParentFragment())).grabCheckpoints();
    }

    /**
     * This grabs the SelectionsStateManager, stored within the parent fragment (ActiveRacerFragment)
     * @return SelectionsStateManager, grabbed from the parent fragment.
     */
    @Override
    public SelectionsStateManager grabSelectionManager() {
         return ((ActiveRacerFragment) Objects.requireNonNull(getParentFragment())).getSelectionsStateManager();
    }

    /**
     * This grabs the DisplayFilterManager, stored within the parent fragment (ActiveRacerFragment)
     * @return DisplayFilterManager, grabbed from the parent fragment.
     */
    //TODO Java doc this
    public DisplayFilterManager grabDisplayFilterManager() {
        return ((ActiveRacerFragment) Objects.requireNonNull(getParentFragment())).getDisplayFilterManager();
    }

}
