package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.RecyclerViewAdapter;

public class RacerTimesFragment extends Fragment implements Observer {
    private final Checkpoint allData = new Checkpoint(1, 150);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.racer_times_fragment,container,false);

        RecyclerView recView = view.findViewById(R.id.changesRecyclerView);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recView.setAdapter(new RecyclerViewAdapter(allData, this.getContext()));

        allData.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                //reload view here
            }
        });


        return view;
    }

    @Override
    public void update(Observable observable, Object o) {
        //refresh view here
    }
}
