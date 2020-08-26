package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.CheckOffStateManager;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.ReportedRaceTimes;

/**
 * This RecyclerViewAdapter is responsible handling the list of racers and their times.
 */
public class RacerTimesRecyclerViewAdapter extends RecyclerView.Adapter<RacerTimesRecyclerViewHolder> {
    private Checkpoints checkpoints;
    private CheckOffStateManager toDisplay;

    /**
     * Constructor for the view adapter
     * @param passedCheckpoints The checkpoints containing data for which to show in the RecyclerViewAdapter
     * @param mContext context
     */
    public RacerTimesRecyclerViewAdapter(Checkpoints passedCheckpoints, Context mContext) {
        this.checkpoints = passedCheckpoints;
        toDisplay = new CheckOffStateManager(checkpoints, null);
        checkpoints.addObserver((observable, o) -> {
            toDisplay = new CheckOffStateManager(checkpoints, null);
            notifyDataSetChanged();
        });
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
            holder.setRacerTimes(checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(toDisplay.getListToDisplay().get(position)));
            ReportedRaceTimes times = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(toDisplay.getListToDisplay().get(position));
            holder.setCheckBoxListener(times);
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
}
