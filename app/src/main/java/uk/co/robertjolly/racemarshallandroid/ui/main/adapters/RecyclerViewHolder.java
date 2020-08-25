package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.ReportedRaceTimes;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

//TODO Java doc this
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private Button racerButton;
    private CheckBox racerReported;
    private TextView timeTextView1;
    private TextView timeTextView2;
    private TextView timeTextView3;
    private View upperDivider;
    private View lowerDivider;
    private ReportedRaceTimes times;
    private boolean manualChange = false;

    //TODO Java doc this
    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        racerButton = itemView.findViewById(R.id.racerNumberButton);
        racerReported = itemView.findViewById(R.id.reportedCheckBox);
        timeTextView1 = itemView.findViewById(R.id.timeTextView1);
        timeTextView2 = itemView.findViewById(R.id.timeTextView2);
        timeTextView3 = itemView.findViewById(R.id.timeTextView3);
        upperDivider = itemView.findViewById(R.id.upperDivider);
        lowerDivider = itemView.findViewById(R.id.lowerDivider);
    }

    //TODO Java doc this
    public void setRacerButton(String text) {
        racerButton.setText(text);
    }

    //TODO Java doc this
    public void setRacerTimes(ReportedRaceTimes times) {
        setRacerTimeTextBox1(times);
        setRacerTimeTextBox2(times);
        setRacerTimeTextBox3(times);

    }

    //TODO Java doc this
    //TODO These set racer time boxes are dodgy, not very object oriented - I should probably fix that.
    private void setRacerTimeTextBox1(ReportedRaceTimes times) {
       if (times.getRaceTimes().getInTime() != null) {
           timeTextView1.setText(times.getFormattedDisplayTime(TimeTypes.IN));
       } else if (times.getRaceTimes().getOutTime() != null) {
           timeTextView1.setText(times.getFormattedDisplayTime(TimeTypes.OUT));
       } else if (times.getRaceTimes().getDroppedOutTime() != null) {
           timeTextView1.setText(times.getFormattedDisplayTime(TimeTypes.DROPPEDOUT));
       } else if (times.getRaceTimes().getNotStartedTime() != null) {
           timeTextView1.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART));
       }
    }

    //TODO Java doc this
    private void setRacerTimeTextBox2(ReportedRaceTimes times) {
        if (times.getRaceTimes().getInTime() != null) {
            if (times.getRaceTimes().getOutTime() != null) {
                timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.OUT));
            } else if (times.getRaceTimes().getDroppedOutTime() != null) {
                timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DROPPEDOUT));
            } else if (times.getRaceTimes().getNotStartedTime() != null) {
                timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART));
            } else {
                timeTextView2.setText("");
            }
        } else {
            if (times.getRaceTimes().getOutTime() != null) {
                if (times.getRaceTimes().getDroppedOutTime() != null) {
                    timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DROPPEDOUT));
                } else if (times.getRaceTimes().getNotStartedTime() != null) {
                    timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART));
                } else {
                    timeTextView2.setText("");
                }
            } else {
                if ((times.getRaceTimes().getDroppedOutTime() != null) & (times.getRaceTimes().getNotStartedTime() != null) ) {
                    timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART));
                } else {
                    timeTextView2.setText("");
                }
            }
        }
    }

    //TODO Java doc this
    private void setRacerTimeTextBox3(ReportedRaceTimes times) {
        if (timeTextView2.getText() != "") {
            if ((times.getRaceTimes().getInTime() != null) & (times.getRaceTimes().getOutTime() != null)) {
                if (times.getRaceTimes().getDroppedOutTime() != null) {
                    timeTextView3.setText(times.getFormattedDisplayTime(TimeTypes.DROPPEDOUT));
                } else if (times.getRaceTimes().getNotStartedTime() != null){
                    timeTextView3.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART));
                }
            }
        } else {
            timeTextView3.setText("");
        }
    }

    //TODO Java doc this
    public void setCheckBoxListener(final ReportedRaceTimes times) {
        manualChange = true;
        racerReported.setChecked(times.allReported());
        manualChange = false;

        racerReported.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!manualChange) {
                    if (b) {
                        times.setAllUnreportedToReported();
                    } else {
                        times.setAllReportedToUnreported();
                    }
                    setRacerTimes(times);
                }
            }
        });
    }

    //TODO Java doc this
    public void setBoxStylesAndTime(ReportedRaceTimes previousItem, ReportedRaceTimes currentItem) {

    }

    //TODO Java doc this
    public void makeInvisible() {
        racerButton.setVisibility(View.INVISIBLE);
        racerReported.setVisibility(View.INVISIBLE);
        timeTextView1.setVisibility(View.INVISIBLE);
        timeTextView2.setVisibility(View.INVISIBLE);
        timeTextView3.setVisibility(View.INVISIBLE);
        lowerDivider.setVisibility(View.INVISIBLE);
        upperDivider.setVisibility(View.INVISIBLE);
    }

    //TODO Java doc this
    public void makeUninvisible() {
        racerButton.setVisibility(View.VISIBLE);
        racerReported.setVisibility(View.VISIBLE);
        timeTextView1.setVisibility(View.VISIBLE);
        timeTextView2.setVisibility(View.VISIBLE);
        timeTextView3.setVisibility(View.VISIBLE);
        lowerDivider.setVisibility(View.VISIBLE);
        upperDivider.setVisibility(View.VISIBLE);
    }

}
