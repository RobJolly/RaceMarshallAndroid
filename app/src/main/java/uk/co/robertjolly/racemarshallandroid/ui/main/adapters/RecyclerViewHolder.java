package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView racerText;
    private CheckBox racerReported;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        racerText = itemView.findViewById(R.id.racerDataTextView);
        racerReported = itemView.findViewById(R.id.reportedCheckBox);
    }

    public void setRacerText(String text) {
        racerText.setText(text);
    }
}
