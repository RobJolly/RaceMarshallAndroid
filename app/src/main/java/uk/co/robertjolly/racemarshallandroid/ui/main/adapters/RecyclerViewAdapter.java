package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Racer;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Checkpoint test;
    private Context mContext;

    private ArrayList<Racer> notPassed;
    public RecyclerViewAdapter(Checkpoint passedCheckpoint, Context mContext) {
        this.test = passedCheckpoint;
        this.mContext = mContext;
        notPassed = passedCheckpoint.getAllNotPassed();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.racer_passed_data_display, parent, false);

        int i = 0;
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.setRacerText(String.valueOf(notPassed.get(position).getRacerNumber()));
    }

    @Override
    public int getItemCount() {
        return notPassed.size();
    }
}
