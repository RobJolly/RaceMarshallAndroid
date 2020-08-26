package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

//Documentation: https://developer.android.com/reference/com/google/android/material/floatingactionbutton/FloatingActionButton
//Android material components, https://github.com/material-components/material-components-android, Apache 2.0 License.
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.CheckpointFob;

//TODO Java doc this
public class ActiveRacerFragment extends Fragment implements CheckpointGrabber {
    private SelectionsStateManager selectionsStateManager;
    private DisplayFilterManager displayFilterManager;

    //TODO Java doc this
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            try {
                SelectionsStateManager test = (SelectionsStateManager) savedInstanceState.get("selectionsStateManager");
                test.setCheckpoints(grabCheckpoints());
                setSelectionsStateManager(test);
            } catch (Exception e) { //something gone wrong with the bundle - go with the backup loading
                setSelectionsStateManager(new SelectionsStateManager(grabCheckpoints()));
            }
            try {
                setDisplayFilterManager((DisplayFilterManager) savedInstanceState.get("displayFilterManager"));
            } catch (Exception e) { //something gone wrong with the bundle - go with the backup loading
                setDisplayFilterManager(new DisplayFilterManager());
            }
        } else {
            setSelectionsStateManager(new SelectionsStateManager(grabCheckpoints()));
            setDisplayFilterManager(new DisplayFilterManager());
        }
        super.onCreate(savedInstanceState);

    }

    //TODO Java doc this
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.active_racers_portrait_fragment,container,false);
        } else {
            view = inflater.inflate(R.layout.active_racers_sideways_fragment,container,false);
        }
        view.setTag("ActiveRacerFragment");

        FloatingActionButton fobFilter = (FloatingActionButton) view.findViewById(R.id.filterFob);
        fobFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
                dialogBuilder.setTitle(R.string.FilterRacers);
                dialogBuilder.setCancelable(true);

                dialogBuilder.setMultiChoiceItems(getDisplayFilterManager().getFilterNames(getResources()), getDisplayFilterManager().getBooleanFilterList(), new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        getDisplayFilterManager().changeFilter(i, b);
                        getDisplayFilterManager().notifyObservers();
                    }
                });
                AlertDialog fobDialog = dialogBuilder.create();

                fobDialog.show();

                ListView listView = fobDialog.getListView();
                //int count = listView.getChildCount();
                //fobDialog.getOwnerActivity().
                //Button checkIn = (Button) listView.getItemAtPosition(0);
               // fobDialog.getListView().findViewsWithText(viewArrayList, getResources().getString(R.string.checkedIn), 0);
               // int i = 0;
            }
        });

        CheckpointFob.createCheckpointFob(view, getActivity(), grabCheckpoints());

        return view;
    }

    //TODO Java doc this
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //TODO Java doc this
    @Override
    public Checkpoints grabCheckpoints() {
        return ((SectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).getCheckpoints();
    }

    //TODO Java doc this
    public SelectionsStateManager getSelectionsStateManager() {
        return selectionsStateManager;
    }

    /*
    public SelectionsStateManager grabSelectionsStateManager() {
        return ((SectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).getSelectionsStateManager();
    }*/

    //TODO Java doc this
    public DisplayFilterManager getDisplayFilterManager() {
        return displayFilterManager;
    }

    public void setSelectionsStateManager(SelectionsStateManager selectionsStateManager) {
        this.selectionsStateManager = selectionsStateManager;
    }

    public void setDisplayFilterManager(DisplayFilterManager displayFilterManager) {
        this.displayFilterManager = displayFilterManager;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectionsStateManager", selectionsStateManager);
        outState.putParcelable("displayFilterManager", displayFilterManager);
    }


}
