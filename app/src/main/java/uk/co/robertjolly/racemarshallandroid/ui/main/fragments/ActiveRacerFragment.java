package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;

public class ActiveRacerFragment extends Fragment implements CheckpointGrabber, SelectionManagerGrabber {
    private ArrayList<RacerDisplayFilter> racerFilters;
    private SelectionsStateManager selectionsStateManager;
    private DisplayFilterManager displayFilterManager = new DisplayFilterManager();

    private int selectedCheckpoint = 1; //this will be changed when checkpoints are implemented

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    this.racers = getRacers();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectionsStateManager = new SelectionsStateManager(grabCheckpoints().getCheckpoint(selectedCheckpoint));
        View view;

        //find orientation of screen
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.active_racers_portrait_fragment,container,false);
        } else {
            view = inflater.inflate(R.layout.active_racers_sideways_fragment,container,false);
        }

        FloatingActionButton fob = (FloatingActionButton) view.findViewById(R.id.filterFob);
        fob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle(R.string.FilterRacers);
                dialogBuilder.setCancelable(true);

                dialogBuilder.setMultiChoiceItems(grabDisplayFilterManager().getFilterNames(getResources()), grabDisplayFilterManager().getBooleanFilterList(), new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        grabDisplayFilterManager().changeFilter(i, b);
                        grabDisplayFilterManager().notifyObservers();
                    }
                });
                dialogBuilder.show();
            }
        });
        return view;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public Checkpoints grabCheckpoints() {
        return ((SectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).grabCheckpoints();
    }

    @Override
    public SelectionsStateManager grabSelectionManager() {
        return selectionsStateManager;
    }

    public DisplayFilterManager grabDisplayFilterManager() {
        return displayFilterManager;
    }
}
