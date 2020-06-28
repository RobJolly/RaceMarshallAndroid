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
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import uk.co.robertjolly.racemarshallandroid.R;

public class ActiveRacerFragment extends Fragment {
    private static final String TAG = "ActiveRacerFragment";
    private ArrayList<Integer> racers;
   /* public ActiveRacerFragment(ArrayList<Integer> passedRacers) {
        this.racers = passedRacers;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.racers = getRacers();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int currentOrientation = getResources().getConfiguration().orientation;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int i = 0;
            View view = inflater.inflate(R.layout.active_racers_portrait_fragment,container,false);
            int j = 0;
            Bundle test = new Bundle();
            test.putIntegerArrayList("racers", racers);
            ((ActiveRacerDisplayFragment) getChildFragmentManager().findFragmentById(R.id.selectionFragment)).setRacers(racers);
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


}
