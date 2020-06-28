package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.RaceGridViewAdapter;

public class ActiveRacerDisplayFragment extends Fragment {
    private static final String TAG = "ActiveRacerDisplayFragment";
    private ArrayList<Integer> racers = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Fragment frag = getParentFragment();
        racers = (ArrayList<Integer>) frag.getArguments().get("racers");
        
        View view = inflater.inflate(R.layout.active_racers_display_fragment, container, false);
        view.setElevation(0); //doesn't work when set in XML - I'm unsure why, but I should fix this later when I know.

        final GridView gridView = ((GridView) view.findViewById(R.id.gridView));
        final RaceGridViewAdapter raceGridViewAdapter = new RaceGridViewAdapter(gridView.getContext(), racers);
        gridView.setAdapter(raceGridViewAdapter);

        int j = 0;
        return view;
    }

    private ArrayList<Integer> getRacers() {
        ArrayList<Integer> racers = new ArrayList<>();
        /*for (int i = 0; i < 121; i++) {
            racers.add(i);
        }
        return racers;*/
      if (getArguments() != null) {
          ArrayList<Integer> test = (ArrayList<Integer>) getArguments().get("racers");
          return test;
      } else {
          return racers;
      }
      //  ArrayList<Integer> test = (ArrayList<Integer>) getArguments().get("racers");
       // int i = 0;
     //   return racers;
    }

    public void setRacers(ArrayList<Integer> passedRacers) {
        this.racers = passedRacers;
    }
    public void changeBackground() {
    }

}