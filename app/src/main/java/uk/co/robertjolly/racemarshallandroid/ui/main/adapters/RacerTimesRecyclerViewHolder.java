package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.content.res.Resources;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

//Projects own classes.
import java.util.Objects;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.ReportedRaceTimes;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.EditRacerDialogFragment;

/**
 * This is the class that handles setting a single segment of the recycler view displaying racer times.
 * It is responsible for setting the text boxes, check boxes, text views, etc.
 */
public class RacerTimesRecyclerViewHolder extends RecyclerView.ViewHolder {

    private Button racerButton;
    private CheckBox racerReported;
    private TextView timeTextView1;
    private TextView timeTextView2;
    private TextView timeTextView3;
    private View upperDivider;
    private View lowerDivider;
    private boolean manualChange = false;

    /**
     * Constructor for the recyclerViewHolder. Grabs some items from the view.
     * @param itemView the view to set/grab data from
     */
    public RacerTimesRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        racerButton = itemView.findViewById(R.id.racerNumberButton);
        racerReported = itemView.findViewById(R.id.reportedCheckBox);
        timeTextView1 = itemView.findViewById(R.id.timeTextView1);
        timeTextView2 = itemView.findViewById(R.id.timeTextView2);
        timeTextView3 = itemView.findViewById(R.id.timeTextView3);
        upperDivider = itemView.findViewById(R.id.upperDivider);
        lowerDivider = itemView.findViewById(R.id.lowerDivider);
    }

    /**
     * This sets the racer button text to the given text
     * @param text The text to set
     */
    public void setRacerButton(String text) {
        racerButton.setText(text);
    }

    public void setRacerButtonListener(FragmentManager fragmentManager, Checkpoints checkpoints, Racer racer) {
        racerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditRacerDialogFragment editFragment = new EditRacerDialogFragment(checkpoints, racer);
                editFragment.show(fragmentManager, "Edit Fragment");
            }
        });
    }

    /**
     * This sets the times for the racer text boxes, based on the given times.
     * @param times the times of the racer to set.
     */
    public void setRacerTimes(ReportedRaceTimes times, Resources resources) {
        setRacerTimeTextBox1(times, resources);
        setRacerTimeTextBox2(times, resources);
        setRacerTimeTextBox3(times, resources);
    }

    /**
     * This sets the text for the first racerTimes TextView. TextViews should be set in-order.
     * @param times The times from which to calculate the text of the TextView from.
     */
    //TODO These set racer time boxes are dodgy, not very object oriented, or clean - I should probably fix that, but it's not a priority.
    private void setRacerTimeTextBox1(ReportedRaceTimes times, Resources resources) {
       if (times.getRaceTimes().getInTime() != null) {
           timeTextView1.setText(times.getFormattedDisplayTime(TimeTypes.IN, resources));
           if (times.inReportedIfSet()) {
               timeTextView1.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
           } else {
               timeTextView1.setPaintFlags(0);
           }
       } else if (times.getRaceTimes().getOutTime() != null) {
           timeTextView1.setText(times.getFormattedDisplayTime(TimeTypes.OUT, resources));
           if (times.outReportedIfSet()) {
               timeTextView1.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
           } else {
               timeTextView1.setPaintFlags(0);
           }
       } else if (times.getRaceTimes().getDroppedOutTime() != null) {
           timeTextView1.setText(times.getFormattedDisplayTime(TimeTypes.DROPPEDOUT, resources));
           if (times.droppedOutReportedIfSet()) {
               timeTextView1.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
           } else {
               timeTextView1.setPaintFlags(0);
           }
       } else if (times.getRaceTimes().getNotStartedTime() != null) {
           timeTextView1.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART, resources));
           if (times.didNotStartReportedIfSet()) {
               timeTextView1.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
           } else {
               timeTextView1.setPaintFlags(0);
           }
       }
    }

    /**
     * This sets the text for the first racerTimes TextView. TextViews should be set in-order.
     * @param times The times from which to calculate the text of the TextView from.
     */
    private void setRacerTimeTextBox2(ReportedRaceTimes times, Resources resources) {
        if (times.getRaceTimes().getInTime() != null) {
            if (times.getRaceTimes().getOutTime() != null) {
                timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.OUT, resources));
                if (times.outReportedIfSet()) {
                    timeTextView2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    timeTextView2.setPaintFlags(0);
                }
            } else if (times.getRaceTimes().getDroppedOutTime() != null) {
                timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DROPPEDOUT, resources));
                if (times.droppedOutReportedIfSet()) {
                    timeTextView2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    timeTextView2.setPaintFlags(0);
                }
            } else if (times.getRaceTimes().getNotStartedTime() != null) {
                timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART, resources));
                if (times.didNotStartReportedIfSet()) {
                    timeTextView2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    timeTextView2.setPaintFlags(0);
                }
            } else {
                timeTextView2.setText("");
                timeTextView2.setPaintFlags(0);
            }
        } else {
            if (times.getRaceTimes().getOutTime() != null) {
                if (times.getRaceTimes().getDroppedOutTime() != null) {
                    timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DROPPEDOUT, resources));
                    if (times.droppedOutReportedIfSet()) {
                        timeTextView2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        timeTextView2.setPaintFlags(0);
                    }
                } else if (times.getRaceTimes().getNotStartedTime() != null) {
                    timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART, resources));
                    if (times.didNotStartReportedIfSet()) {
                        timeTextView2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        timeTextView2.setPaintFlags(0);
                    }
                } else {
                    timeTextView2.setText("");
                    timeTextView2.setPaintFlags(0);
                }
            } else {
                if ((times.getRaceTimes().getDroppedOutTime() != null) & (times.getRaceTimes().getNotStartedTime() != null) ) {
                    timeTextView2.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART, resources));
                    if (times.didNotStartReportedIfSet()) {
                        timeTextView2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        timeTextView2.setPaintFlags(0);
                    }
                } else {
                    timeTextView2.setText("");
                    timeTextView2.setPaintFlags(0);
                }
            }
        }
    }

    /**
     * This sets the text for the first racerTimes TextView. TextViews should be set in-order.
     * @param times The times from which to calculate the text of the TextView from.
     */
    private void setRacerTimeTextBox3(ReportedRaceTimes times, Resources resources) {
        if (timeTextView2.getText() != "") {
            if ((times.getRaceTimes().getInTime() != null) & (times.getRaceTimes().getOutTime() != null)) {
                if (times.getRaceTimes().getDroppedOutTime() != null) {
                    timeTextView3.setText(times.getFormattedDisplayTime(TimeTypes.DROPPEDOUT, resources));
                    if (times.droppedOutReportedIfSet()) {
                        timeTextView3.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        timeTextView3.setPaintFlags(0);
                    }
                } else if (times.getRaceTimes().getNotStartedTime() != null){
                    timeTextView3.setText(times.getFormattedDisplayTime(TimeTypes.DIDNOTSTART, resources));
                    if (times.didNotStartReportedIfSet()) {
                        timeTextView3.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        timeTextView3.setPaintFlags(0);
                    }
                }
            }
        } else {
            timeTextView3.setText("");
            timeTextView3.setPaintFlags(0);
        }
    }

    /**
     * This creates the listener for the check box. Allows setting the times for the racer, to reported or unreported if checked.
     * @param times the times of the racer associated with the view.
     */
    public void setCheckBoxListener(final ReportedRaceTimes times, Resources resources) {
        manualChange = true;
        racerReported.setChecked(times.allReported());
        manualChange = false;

        racerReported.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!manualChange) {
                if (b) {
                    times.setAllUnreportedToReported();
                } else {
                    times.setAllReportedToUnreported();
                }
                setRacerTimes(times, resources);
            }
        });
    }

    /**
     * This makes all items in the view invisible.
     */
    public void makeInvisible() {
        racerButton.setVisibility(View.INVISIBLE);
        racerReported.setVisibility(View.INVISIBLE);
        timeTextView1.setVisibility(View.INVISIBLE);
        timeTextView2.setVisibility(View.INVISIBLE);
        timeTextView3.setVisibility(View.INVISIBLE);
        lowerDivider.setVisibility(View.INVISIBLE);
        upperDivider.setVisibility(View.INVISIBLE);
    }

    /**
     * This makes all items in the view visible.
     */
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
