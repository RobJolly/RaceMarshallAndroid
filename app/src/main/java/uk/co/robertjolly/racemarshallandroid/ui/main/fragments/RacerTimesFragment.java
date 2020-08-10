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
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.RecyclerViewAdapter;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.checkpointFob;

public class RacerTimesFragment extends Fragment implements Observer, CheckpointGrabber {
    private Checkpoints checkpoints = new Checkpoints();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // grabCheckpoints().getCheckpoint(1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        checkpoints = grabCheckpoints();

        View view = inflater.inflate(R.layout.racer_times_fragment,container,false);

        final RecyclerView recView = view.findViewById(R.id.changesRecyclerView);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recView.setAdapter(new RecyclerViewAdapter(checkpoints, this.getContext()));

        checkpoints.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                recView.getAdapter().notifyDataSetChanged();
                //recView.getAdapter().notifyDataSetChanged();
            }
        });

        checkpointFob.createCheckpointFob(view, getActivity(), checkpoints);
     /*   allData.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                //reload view here
            }
        });*/


        return view;
    }

    @Override
    public void update(Observable observable, Object o) {
        //refresh view here
    }

    @Override
    public Checkpoints grabCheckpoints() {

        ViewPager pager = getActivity().findViewById(R.id.mainViewPager);
        SectionsPagerAdapter adapter = (SectionsPagerAdapter) pager.getAdapter();
        return adapter.grabCheckpoints();
       // Checkpoint checkpoint = checkpoints.getCheckpoint(1);
       // int i = 0;
       // return ((SectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).grabCheckpoints();
    }
}
