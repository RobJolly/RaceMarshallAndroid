package uk.co.robertjolly.racemarshallandroid.ui.main.customElements;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

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
import uk.co.robertjolly.racemarshallandroid.miscClasses.Vibrate;

public class ModifyTimeButton extends androidx.appcompat.widget.AppCompatButton {

    MyTimePickerDialog openPickerDialog;
    Observer checkpointObserver;
    Checkpoints checkpoints;
    /**
     * Creates the button
     * @param context
     */
    public ModifyTimeButton(@NonNull Context context) {
        super(context);
    }

    /**
     * Creates the button
     * @param context
     * @param attrs
     */
    public ModifyTimeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates the button
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public ModifyTimeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * This is used to initialise the values of the time button, and what interacting with it shall change.
     * Note that changes will happen only to the currently selected checkpoint.
     * @param checkpointsToEdit The checkpoints the time button should interact with.
     * @param racerToEdit The racer that the button is intended to interact with.
     * @param timeTypeToEdit The time the button is intended to show and change
     */
    public void initialiseButton(Checkpoints checkpointsToEdit, Racer racerToEdit, TimeTypes timeTypeToEdit) {
        checkpoints = checkpointsToEdit;
        updateButtonText(checkpointsToEdit, racerToEdit, timeTypeToEdit); //shows the text for the button, as a formatted time or message

        setOnClickListener(new OnClickListener() {  //what the button does on click.
            @Override
            public void onClick(View view) {
                ReportedRaceTimes timesToEdit  = Objects.requireNonNull(checkpointsToEdit.getCurrentSelectedCheckpoint()).getRacerData(racerToEdit);
                Date timeOfRacer = timesToEdit.getRaceTimes().getTimeOfType(timeTypeToEdit);

                openPickerDialog = new MyTimePickerDialog(view.getContext(), (view1, hourOfDay, minute, seconds) -> { //opens a time picker dialog
                    Date timeShown = Calendar.getInstance().getTime();
                    timeShown.setHours(hourOfDay);
                    timeShown.setMinutes(minute);
                    timeShown.setSeconds(seconds);

                    //Error checking, make sure the user means it if it's been reported before here.
                    if (timesToEdit.getReportedItems().getReportedItem(timeTypeToEdit)) {
                        AlertDialog.Builder warningBuilder = new AlertDialog.Builder(getContext());
                        warningBuilder.setMessage(getResources().getString(R.string.check_time_change_reported));
                        warningBuilder.setPositiveButton(getResources().getString(R.string.cancel), null);
                        warningBuilder.setNegativeButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
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
                        //this just gets time values from a date.
                        timeOfRacer != null ? timeOfRacer.getHours() : Calendar.getInstance().getTime().getHours(),
                        timeOfRacer != null ? timeOfRacer.getMinutes() : Calendar.getInstance().getTime().getMinutes(),
                        timeOfRacer != null ? timeOfRacer.getSeconds() : Calendar.getInstance().getTime().getSeconds(), true);

                openPickerDialog.show();
            }
        });

        checkpointObserver = new Observer() { //whenever checkpoints change, check this button is up-to-date.
            @Override
            public void update(Observable observable, Object o) {
                updateButtonText(checkpointsToEdit, racerToEdit, timeTypeToEdit);
                setEnabled(shouldBeEnabled(checkpointsToEdit.getCurrentSelectedCheckpoint().getRacerData(racerToEdit).getRaceTimes(), timeTypeToEdit));
            }
        };
        checkpointsToEdit.addObserver(checkpointObserver);

        //ensure that the button should be intractable, and change it to be so (or not so).
        setEnabled(shouldBeEnabled(checkpointsToEdit.getCurrentSelectedCheckpoint().getRacerData(racerToEdit).getRaceTimes(), timeTypeToEdit));
    }

    /**
     * This updates the text of the button. If the given time is not set for the racer, then it shall default to a
     * not set message. Otherwise, the time shall be shown in HH:mm:ss format.
     * @param checkpointsToEdit The checkpoints from which to get information. Uses the currently selected checkpoint.
     * @param racerToEdit The racer from which to get information for.
     * @param timeTypeToEdit The type of time to display on the button.
     */
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

    /**
     * This changes the time of a racer, to a given time.
     * @param checkpointsToEdit The checkpoints object to edit, uses the currently selected checkpoint.
     * @param racerToEdit The racer to change the time for.
     * @param timeTypeToEdit The type of time to change.
     * @param timeShown The time to change this to.
     */
    private void changeRacerTime(Checkpoints checkpointsToEdit, Racer racerToEdit, TimeTypes timeTypeToEdit, Date timeShown) {
        new Vibrate().pulse(getContext());
        checkpointsToEdit.setRacerTime(checkpointsToEdit.getCurrentCheckpointNumber(), racerToEdit, timeTypeToEdit, timeShown);
        checkpointsToEdit.notifyObservers();
    }

    /**
     * This determines whether or not this button should be enabled, as clickable, to the user.
     * This would return false in the case that clicking it makes no sense (e.g. this button represents an In-time for a racer that did not start)
     * @param timesToCheck The times for a racer being shown
     * @param typeToCheck The type of time this button represents.
     * @return Whether or not this button should be intractable. True if it should be.
     */
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

    /**
     * This removes any observers generated by creating this object.
     * Useful when finished with the object.
     */
    public void removeObservers() {
        checkpoints.deleteObserver(checkpointObserver);
    }


}
