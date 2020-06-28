package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import uk.co.robertjolly.racemarshallandroid.R;

public class ActiveRacerFragment extends Fragment {
    private static final String TAG = "ActiveRacerFragment";
   // private final ArrayList<Integer> racers;

   /* public ActiveRacerFragment(ArrayList<Integer> passedRacers) {
        this.racers = passedRacers;
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int currentOrientation = getResources().getConfiguration().orientation;
      //  Bundle arg = getArguments();

        //switch fragment based on orientation
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


}
