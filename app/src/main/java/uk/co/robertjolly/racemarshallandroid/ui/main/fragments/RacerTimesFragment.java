package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.MainActivity;
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.TimesFilterManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.RacerTimesRecyclerViewAdapter;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.CheckpointFab;

//TODO Java doc this
public class RacerTimesFragment extends Fragment implements Observer, CheckpointGrabber {
    private Checkpoints checkpoints = new Checkpoints();
    private TimesFilterManager timesFilterManager;
    private Observer checkpointsObserver; //these two are stored for the purposes of removing observers.
    private RacerTimesRecyclerViewAdapter viewAdapter;

    //TODO Java doc this
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkpoints = grabCheckpoints();
        timesFilterManager = new TimesFilterManager();
    }

    /**
     * This is the function that is run after onCreate, but before the view is shown to the user.
     * This initialises the view with the information that should be shown to the user.
     * The purpose of this is to create a view where the user can view the racer times, report them if desired and
     * edit them if needed.
     * @param inflater the inflater.
     * @param container the container.
     * @param savedInstanceState The bundle in which data is saved and loaded from any previous views, if any.
     * @return view to be shown to user, with information set
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.racer_times_fragment,container,false);

        final RecyclerView recView = view.findViewById(R.id.changesRecyclerView);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewAdapter = new RacerTimesRecyclerViewAdapter(checkpoints, getFragmentManager(), timesFilterManager, getResources());
        recView.setAdapter(viewAdapter);

        checkpointsObserver = (observable, o) -> {
            try {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> Objects.requireNonNull(recView.getAdapter()).notifyDataSetChanged());
            } catch (Exception e) {
                Log.w("Warning", "Cold not notify the recycler view of the checkpoint update.");
            }
        };

        checkpoints.addObserver(checkpointsObserver);

        CheckpointFab.createCheckpointFob(view, getActivity(), checkpoints);
        FloatingActionButton filterFab = view.findViewById(R.id.filterFob);
        filterFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
                dialogBuilder.setTitle(R.string.filter_racers);
                dialogBuilder.setCancelable(true);

                //Multi choice dialog. So users can pick from many.
                dialogBuilder.setMultiChoiceItems(getTimesFilterManager().getFilterNames(getResources()), getTimesFilterManager().getBooleanFilterList(), (dialogInterface, i, b) -> {
                    getTimesFilterManager().changeFilter(i, b);
                    getTimesFilterManager().notifyObservers();
                });
                AlertDialog fobDialog = dialogBuilder.create();
                fobDialog.show();
            }
        });

        return view;
    }


    /**
     * Required function for Fragment extension.
     * Does not do anything.
     * @param observable -
     * @param o -
     */
    @Override
    public void update(Observable observable, Object o) {
        //refresh view here
    }

    /**
     * Grabber for the checkpoints. Grabs this from MainActivity
     * @return Checkpoints grabbed from MainActivity
     */
    @Override
    public Checkpoints grabCheckpoints() {
        MainActivity activity = (MainActivity) getActivity(); //Yes, this is not ideal. For my purposes it works.
        return Objects.requireNonNull(activity).getCheckpoints();
    }

    public TimesFilterManager getTimesFilterManager() {
        return timesFilterManager;
    }

    //this should remove all observers created by this and its children.
    public void removeObservers() {
        checkpoints.deleteObserver(checkpointsObserver);
        viewAdapter.removeObservers();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //removeObservers(); //this deletes all observers before it's remade. Prevents continuously growing the number of observers on things such as screen rotation.
        super.onSaveInstanceState(outState);
    }
}
