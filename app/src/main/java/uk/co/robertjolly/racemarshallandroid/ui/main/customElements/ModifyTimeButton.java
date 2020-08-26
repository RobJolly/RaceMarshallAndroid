package uk.co.robertjolly.racemarshallandroid.ui.main.customElements;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.RaceTimes;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.ReportedRaceTimes;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

public class ModifyTimeButton extends androidx.appcompat.widget.AppCompatButton {

    MyTimePickerDialog openPickerDialog;
    Observer checkpointObserver;
    Checkpoints checkpoints;
    /**
     * Creates the button
     * @param context context
     */
    public ModifyTimeButton(@NonNull Context context) {
        super(context);
    }

    public ModifyTimeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModifyTimeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialiseButton(Checkpoints checkpointsToEdit, Racer racerToEdit, TimeTypes timeTypeToEdit) {
        checkpoints = checkpointsToEdit;
        updateButtonText(checkpointsToEdit, racerToEdit, timeTypeToEdit);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportedRaceTimes timesToEdit  = Objects.requireNonNull(checkpointsToEdit.getCurrentSelectedCheckpoint()).getRacerData(racerToEdit);
                Date timeOfRacer = timesToEdit.getRaceTimes().getTimeOfType(timeTypeToEdit);

                openPickerDialog = new MyTimePickerDialog(view.getContext(), (view1, hourOfDay, minute, seconds) -> {
                    Date timeShown = Calendar.getInstance().getTime();
                    timeShown.setHours(hourOfDay);
                    timeShown.setMinutes(minute);
                    timeShown.setSeconds(seconds);

                    //Error checking, make sure they mean if it's been reported before here.
                    if (timesToEdit.getReportedItems().getReportedItem(timeTypeToEdit)) {
                        AlertDialog.Builder warningBuilder = new AlertDialog.Builder(getContext());
                        warningBuilder.setMessage("The previous time has already been reported. Are you sure you want to change it?");
                        warningBuilder.setPositiveButton("Cancel", null);
                        warningBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeRacerTime(checkpointsToEdit, racerToEdit, timeTypeToEdit, timeShown);
                                checkpoints.getCurrentSelectedCheckpoint().getRacerData(racerToEdit).getReportedItems().setReportedItem(timeTypeToEdit, false);
                                openPickerDialog = null;
                            }
                        });
                        warningBuilder.create().show();
                    } else {
                        changeRacerTime(checkpointsToEdit, racerToEdit, timeTypeToEdit, timeShown);
                    }
                },
                        // I'm using these despite it being deprecated, as I can't find a simple alternative.
                        timeOfRacer != null ? timeOfRacer.getHours() : Calendar.getInstance().getTime().getHours(),
                        timeOfRacer != null ? timeOfRacer.getMinutes() : Calendar.getInstance().getTime().getMinutes(),
                        timeOfRacer != null ? timeOfRacer.getSeconds() : Calendar.getInstance().getTime().getSeconds(), true);

                openPickerDialog.show();
            }
        });

        checkpointObserver = new Observer() { //whenever checkpoints change, check this.
            @Override
            public void update(Observable observable, Object o) {
                updateButtonText(checkpointsToEdit, racerToEdit, timeTypeToEdit);
                setEnabled(shouldBeEnabled(checkpointsToEdit.getCurrentSelectedCheckpoint().getRacerData(racerToEdit).getRaceTimes(), timeTypeToEdit));
            }
        };
        checkpointsToEdit.addObserver(checkpointObserver);

        setEnabled(shouldBeEnabled(checkpointsToEdit.getCurrentSelectedCheckpoint().getRacerData(racerToEdit).getRaceTimes(), timeTypeToEdit));
    }

    private void updateButtonText(Checkpoints checkpointsToEdit, Racer racerToEdit, TimeTypes timeTypeToEdit) {
        ReportedRaceTimes timesToEdit  = Objects.requireNonNull(checkpointsToEdit.getCurrentSelectedCheckpoint()).getRacerData(racerToEdit);
        Date timeOfRacer = timesToEdit.getRaceTimes().getTimeOfType(timeTypeToEdit);
        if (timeOfRacer == null) {
            setText(R.string.not_set);
        } else {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            setText(timeFormat.format(timeOfRacer));
        }
    }

    private void changeRacerTime(Checkpoints checkpointsToEdit, Racer racerToEdit, TimeTypes timeTypeToEdit, Date timeShown) {
        checkpointsToEdit.setRacerTime(checkpointsToEdit.getCurrentCheckpointNumber(), racerToEdit, timeTypeToEdit, timeShown);
        checkpointsToEdit.notifyObservers();
    }

    private boolean shouldBeEnabled(RaceTimes timesToCheck, TimeTypes typeToCheck) {
        //return timesToCheck.getTimeOfType(typeToCheck) != null;
        switch (typeToCheck) {
            case IN:
            case OUT:
            case DROPPEDOUT:
                return timesToCheck.getNotStartedTime() == null;
            case DIDNOTSTART:
                return timesToCheck.getDroppedOutTime() == null && timesToCheck.getOutTime() == null && timesToCheck.getInTime() == null;
            default:
                Log.e("Error", "Attempted to find information about a time type that is not checked for. Returned False.");
                return false;
        }
    }

    public void removeObservers() {
        checkpoints.deleteObserver(checkpointObserver);
    }


}
