package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

//Documentation: https://developer.android.com/reference/com/google/android/material/floatingactionbutton/FloatingActionButton
//Android material components, https://github.com/material-components/material-components-android, Apache 2.0 License.
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.util.Objects;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.MainTabsSectionsPagerAdapter;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.CheckpointFab;

/**
 * This is the activeRacerFragment. This fragment concerns itself with the selection, and mass modification,
 * of racers within the selected checkpoint.
 */
public class ActiveRacerFragment extends Fragment implements CheckpointGrabber {
    private SelectionsStateManager selectionsStateManager;
    private DisplayFilterManager displayFilterManager;

    /**
     * This function is called upon the creation of the fragment, and is to initialise its data.
     * Some data will be taken from the savedInstanceState if available.
     * @param savedInstanceState The stored data from a previous instance of this fragment
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        /*
        This tries to get the SelectionsStateManager and DisplayFilterManager from the previous fragment.
        If none exists or there are errors in getting them, it shall create new ones.
         */
        if (savedInstanceState != null) {
            try {
                SelectionsStateManager test = (SelectionsStateManager) savedInstanceState.get("selectionsStateManager");
                Objects.requireNonNull(test).setCheckpoints(grabCheckpoints());
                setSelectionsStateManager(test);
            } catch (Exception e) { //something gone wrong with the bundle - go with the backup loading
                setSelectionsStateManager(new SelectionsStateManager(grabCheckpoints()));
                Log.e("ERROR", "Failed to correctly obtain the SelectionsStateManager from the bundle. It has been reset with default values.");
            }
            try {
                setDisplayFilterManager((DisplayFilterManager) savedInstanceState.get("displayFilterManager"));
            } catch (Exception e) { //something gone wrong with the bundle - go with the backup loading
                setDisplayFilterManager(new DisplayFilterManager());
                Log.e("ERROR", "Failed to correctly obtain the DisplayFilterManager from the bundle. It has been reset with default values.");
            }
        } else {
            setSelectionsStateManager(new SelectionsStateManager(grabCheckpoints()));
            setDisplayFilterManager(new DisplayFilterManager());
        }
        super.onCreate(savedInstanceState);

    }

    /**
     * This is the function called prior to showing the view to the user. This concerns itself with
     * the setting of observers, listeners and data to the various components of this view.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //gives this fragment orientation.
            view = inflater.inflate(R.layout.active_racers_portrait_fragment,container,false);
        } else {
            view = inflater.inflate(R.layout.active_racers_sideways_fragment,container,false);
        }
        view.setTag("ActiveRacerFragment"); //set a tag.

        FloatingActionButton fabFilter = view.findViewById(R.id.filterFob); //This allows filters to be selected.
        fabFilter.setOnClickListener(view1 -> {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
            dialogBuilder.setTitle(R.string.FilterRacers);
            dialogBuilder.setCancelable(true);

            //Multi choice dialog. So users can pick from many.
            //TODO If it can be figured out, set the colours of the items within this dialog, to correspond with the colours of their respective racers in the activeRacerDisplay fragment
            dialogBuilder.setMultiChoiceItems(getDisplayFilterManager().getFilterNames(getResources()), getDisplayFilterManager().getBooleanFilterList(), (dialogInterface, i, b) -> {
                getDisplayFilterManager().changeFilter(i, b);
                getDisplayFilterManager().notifyObservers();
            });
            AlertDialog fobDialog = dialogBuilder.create();

            fobDialog.show();

        });

        CheckpointFab.createCheckpointFob(view, getActivity(), grabCheckpoints());
        return view;
    }

    /**
     * This grabs the Checkpoints from the MainTabsSectionsPagerAdapter, within the Activity.
     * @return The Checkpoints grabbed.
     */
    @Override
    public Checkpoints grabCheckpoints() {
        //This is fairly unsafe. But it's quick and works. Be careful with its use.
        return ((MainTabsSectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).getCheckpoints();
    }

    /**
     * This gets the selectionsStateManager stored within this activity.
     * @return The selectionsStateManager stored within this activity.
     */
    public SelectionsStateManager getSelectionsStateManager() {
        return selectionsStateManager;
    }


    /**
     * This gets the DisplayFilterManager stored within this activity.
     * @return The DisplayFilterManager stored within this activity.
     */
    public DisplayFilterManager getDisplayFilterManager() {
        return displayFilterManager;
    }

    /**
     * This sets the SelectionsStateManager stored within this activity.
     * @param selectionsStateManager What the SelectionsStateManager stored within this activity is to be changed to.
     */
    public void setSelectionsStateManager(SelectionsStateManager selectionsStateManager) {
        this.selectionsStateManager = selectionsStateManager;
    }

    /**
     * This sets the DisplayFilterManager stored within this activity.
     * @param displayFilterManager What the DisplayFilterManager stored within this activity is to be changed to.
     */
    public void setDisplayFilterManager(DisplayFilterManager displayFilterManager) {
        this.displayFilterManager = displayFilterManager;
    }

    /**
     * This is called upon the saving of this activity (done on screen rotation and such)
     * This function saves the data to a bundle that will be passed into the new instance when it is re-loaded.
     * @param outState The bundle in which to save this fragments data.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectionsStateManager", selectionsStateManager);
        outState.putParcelable("displayFilterManager", displayFilterManager);
    }


}
