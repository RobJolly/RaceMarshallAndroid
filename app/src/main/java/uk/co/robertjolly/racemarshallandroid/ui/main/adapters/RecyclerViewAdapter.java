package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.PipedOutputStream;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.CheckOffStateManager;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.ReportedRaceTimes;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Checkpoints checkpoints;
    private Context mContext;
    private CheckOffStateManager toDisplay;

   // private ArrayList<Racer> notPassed;
    public RecyclerViewAdapter(Checkpoints passedCheckpoints, Context mContext) {
        this.checkpoints = passedCheckpoints;
        this.mContext = mContext;
        toDisplay = new CheckOffStateManager(checkpoints, null);
        checkpoints.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                toDisplay = new CheckOffStateManager(checkpoints, null);
                notifyDataSetChanged();
            }
        });
        /*toDisplay.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                notifyDataSetChanged();
            }
        });*/
        //notPassed = passedCheckpoint.getAllNotPassed();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.racer_passed_data_display, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.setRacerButton(String.valueOf(toDisplay.getListToDisplay().get(position).getRacerNumber()));
        holder.setRacerTimes(checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(toDisplay.getListToDisplay().get(position)));
        ReportedRaceTimes times = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(toDisplay.getListToDisplay().get(position));
        holder.setCheckBoxListener(times);
    }

    @Override
    public int getItemCount() {
        return toDisplay.getListToDisplay().size();
    }
}
