package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Objects;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;

public class ActiveRacerFragment extends Fragment implements CheckpointGrabber, SelectionManagerGrabber {
    private ArrayList<Integer> racers;
    private SelectionsStateManager selectionsStateManager;
    private int selectedCheckpoint = 1;
   /* public ActiveRacerFragment(ArrayList<Integer> passedRacers) {
        this.racers = passedRacers;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    this.racers = getRacers();


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectionsStateManager = new SelectionsStateManager(grabCheckpoints().getCheckpoint(selectedCheckpoint));


        int currentOrientation = getResources().getConfiguration().orientation;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            View view = inflater.inflate(R.layout.active_racers_portrait_fragment,container,false);
            return view;
        } else {
            View view = inflater.inflate(R.layout.active_racers_sideways_fragment,container,false);
            return view;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private ArrayList<Integer> getRacers() {
        Bundle arguments = getArguments();
        assert arguments != null;
        return (ArrayList<Integer>) arguments.get("racers");
    }


    @Override
    public Checkpoints grabCheckpoints() {
        return ((SectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).grabCheckpoints();
    }

    @Override
    public SelectionsStateManager grabSelectionManager() {
        return selectionsStateManager;
    }
}
