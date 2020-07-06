package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.ReportedItems;
import uk.co.robertjolly.racemarshallandroid.data.ReportedRaceTimes;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private Button racerButton;
    private CheckBox racerReported;
    private TextView timeTextView1;
    private TextView timeTextView2;
    private TextView timeTextView3;
    private ReportedRaceTimes times;
    private boolean manualChange = false;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        racerButton = itemView.findViewById(R.id.racerNumberButton);
        racerReported = itemView.findViewById(R.id.reportedCheckBox);
        timeTextView1 = itemView.findViewById(R.id.timeTextView1);
        timeTextView2 = itemView.findViewById(R.id.timeTextView2);
        timeTextView3 = itemView.findViewById(R.id.timeTextView3);
    }

    public void setRacerButton(String text) {
        racerButton.setText(text);
    }

    public void setRacerTimes(ReportedRaceTimes times) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String inTime = "";
        String outTime = "";
        String droppedOutTime = "";
        String didNotStartTime = "";

        if (times.getRaceTimes().getInTime() != null) {
            inTime = timeFormat.format(times.getRaceTimes().getInTime());
        } else {
            inTime = null;
        }

        if (times.getRaceTimes().getOutTime() != null) {
            outTime = timeFormat.format(times.getRaceTimes().getOutTime());
        } else {
            outTime = null;
        }

        if (times.getRaceTimes().getDroppedOutTime() != null) {
            droppedOutTime = timeFormat.format(times.getRaceTimes().getDroppedOutTime());
        } else {
            droppedOutTime = null;
        }

        if (times.getRaceTimes().getNotStartedTime() != null) {
            didNotStartTime = timeFormat.format(times.getRaceTimes().getNotStartedTime());
        } else {
            didNotStartTime = null;
        }

        setRacerTimeTextBox1(times);
        setRacerTimeTextBox2(times);
        setRacerTimeTextBox3(times);

    }

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

    private void setRacerTimeTextBox3(ReportedRaceTimes times) {
        if (timeTextView2.getText() != "") {
            if ((times.getRaceTimes().getInTime() != null) & (times.getRaceTimes().getOutTime() != null)) {
                if (times.getRaceTimes().getDroppedOutTime() != null) {
                    times.getFormattedDisplayTime(TimeTypes.DROPPEDOUT);
                } else {
                    times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART);
                }
            }
        } else {
            timeTextView3.setText("");
        }
    }

    public void setCheckBoxListener(final ReportedRaceTimes times) {
        manualChange = false;
        racerReported.setChecked(times.allReported());
        manualChange = true;

        racerReported.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    times.setAllUnreportedToReported();
            } else if (manualChange) {
                    times.setAllReportedToUnreported();
                }
                setRacerTimes(times);
            }
        });
    }

}
