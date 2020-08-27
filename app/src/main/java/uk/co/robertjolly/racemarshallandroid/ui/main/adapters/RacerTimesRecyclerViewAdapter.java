package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html

//Projects own classes.
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.CheckOffStateManager;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.ReportedRaceTimes;
import uk.co.robertjolly.racemarshallandroid.data.TimesFilterManager;

/**
 * This RecyclerViewAdapter is responsible handling the list of racers and their times.
 */
public class RacerTimesRecyclerViewAdapter extends RecyclerView.Adapter<RacerTimesRecyclerViewHolder> {
    private Checkpoints checkpoints;
    private CheckOffStateManager toDisplay;
    private FragmentManager fragmentManager;
    private Observer checkpointsObserver;
    private Observer checkoffStateManagerObserver;
    private Resources resources;
    /**
     * Constructor for the view adapter
     * @param passedCheckpoints The checkpoints containing data for which to show in the RecyclerViewAdapter
     * @param parentFragmentManager Fragment manager of the caller
     */
    public RacerTimesRecyclerViewAdapter(Checkpoints passedCheckpoints, FragmentManager parentFragmentManager, TimesFilterManager timesFilterManager, Resources resources) {
        this.checkpoints = passedCheckpoints;
        this.fragmentManager = parentFragmentManager;
        this.resources = resources;
        toDisplay = new CheckOffStateManager(checkpoints, timesFilterManager);

        checkpointsObserver = (observable, o) -> {
            //toDisplay.removeObservers();
            //toDisplay = new CheckOffStateManager(checkpoints, timesFilterManager);
            notifyDataSetChanged();
        };
        checkpoints.addObserver(checkpointsObserver);

        checkoffStateManagerObserver = (observable, o) -> {
            notifyDataSetChanged();
        };
        toDisplay.addObserver(checkoffStateManagerObserver);
    }

    /**
     * This is the creator for the view holder (This is what determines what is shown in each bit of the list)
     * @param parent view group
     * @param viewType flags
     * @return The inflated/Created view holder.
     */
    @NonNull
    @Override
    public RacerTimesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.racer_passed_data_display, parent, false);

        return new RacerTimesRecyclerViewHolder(view);
    }

    /**
     * This is called when a given place in the recycler view is created. This is where the data is set.
     * @param holder the holder for which to use to set data.
     * @param position index in the recyclerView
     */
    @Override
    public void onBindViewHolder(@NonNull RacerTimesRecyclerViewHolder holder, int position) {
        int size = toDisplay.getListToDisplay().size();
        if (position >= size || size == 0) {
            holder.makeInvisible();
        } else {
            holder.setRacerButton(String.valueOf(toDisplay.getListToDisplay().get(position).getRacerNumber()));
            holder.setRacerButtonListener(fragmentManager, checkpoints, toDisplay.getListToDisplay().get(position));
            holder.setRacerTimes(checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(toDisplay.getListToDisplay().get(position)), resources);
            ReportedRaceTimes times = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(toDisplay.getListToDisplay().get(position));
            holder.setCheckBoxListener(times, resources);
            holder.makeUninvisible();
        }
    }

    /**
     * This gets the number of items to display in the recycler view
     * @return The number of items to display
     */
    @Override
    public int getItemCount() {
        return toDisplay.getListToDisplay().size() + 2; //+2 is a buffer, this leaves room to scroll at end for floating actions buttons.
    }

    public void removeObservers() {
        checkpoints.deleteObserver(checkpointsObserver);
        toDisplay.deleteObserver(checkoffStateManagerObserver);
    }
}
