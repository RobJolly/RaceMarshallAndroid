package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
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
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.MainActivity;
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.RacerTimesRecyclerViewAdapter;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.CheckpointFab;

//TODO Java doc this
public class RacerTimesFragment extends Fragment implements Observer, CheckpointGrabber {
    private Checkpoints checkpoints = new Checkpoints();

    //TODO Java doc this
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkpoints = grabCheckpoints();
    }

    /**
     * This is the function that is run after onCreate, but before the view is shown to the user.
     * This initialises the view with the information that should be shown to the user.
     * The purpose of this is to create a view where the user can view the racer times, report them if desired and
     * edit them if needed.
     * @param inflater the inflater.
     * @param container the container.
     * @param savedInstanceState The bundle in which data is saved and loaded from any previous views, if any.
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.racer_times_fragment,container,false);

        final RecyclerView recView = view.findViewById(R.id.changesRecyclerView);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recView.setAdapter(new RacerTimesRecyclerViewAdapter(checkpoints, this.getContext()));

        checkpoints.addObserver((observable, o) -> {
            try {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> Objects.requireNonNull(recView.getAdapter()).notifyDataSetChanged());
            } catch (Exception e) {
                Log.w("Warning", "Cold not notify the recycler view of the checkpoint update.");
            }
        });

        CheckpointFab.createCheckpointFob(view, getActivity(), checkpoints);

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
}
