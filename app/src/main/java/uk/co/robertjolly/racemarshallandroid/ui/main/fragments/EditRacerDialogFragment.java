package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.RaceTimes;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;
import uk.co.robertjolly.racemarshallandroid.miscClasses.Vibrate;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.ModifyTimeButton;

public class EditRacerDialogFragment extends DialogFragment {
    Checkpoints checkpoints;
    Racer racer;
    ModifyTimeButton inModifyButton;
    ModifyTimeButton outModifyButton;
    ModifyTimeButton droppedOutModifyButton;
    ModifyTimeButton notStartedModifyButton;
    ArrayList<Observer> checkpointObservers = new ArrayList<>();


    public EditRacerDialogFragment(Checkpoints checkpoints, Racer racer) {
        this.checkpoints = checkpoints;
        this.racer = racer;
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.checkpoints = savedInstanceState.getParcelable("checkpoints");
            this.racer = savedInstanceState.getParcelable("racer");
        }
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n") //for whatever reason, this is a thing. It doesn't seem to break anything.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.edit_racer_fragment, container, false);

        TextView title = view.findViewById(R.id.racerDescriptionText);
        title.setText(getString(R.string.editing_racer) + racer.getRacerNumber());
        inModifyButton = view.findViewById(R.id.inModifyTimeButton);
        inModifyButton.initialiseButton(checkpoints, racer, TimeTypes.IN);

        outModifyButton = view.findViewById(R.id.outModifyTimeButton);
        outModifyButton.initialiseButton(checkpoints, racer, TimeTypes.OUT);

        droppedOutModifyButton = view.findViewById(R.id.droppedOutModifyTimeButton);
        droppedOutModifyButton.initialiseButton(checkpoints, racer, TimeTypes.DROPPEDOUT);

        notStartedModifyButton = view.findViewById(R.id.notStartedModifyTimeButton);
        notStartedModifyButton.initialiseButton(checkpoints, racer, TimeTypes.DIDNOTSTART);

        Button clearInButton = view.findViewById(R.id.inClearTimeButton);
        clearInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearIfSure(TimeTypes.IN);
            }
        });

        Button clearOutButton = view.findViewById(R.id.outClearTimeButton);
        clearOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearIfSure(TimeTypes.OUT);
            }
        });

        Button clearDroppedOutButton = view.findViewById(R.id.droppedOutClearTimeButton);
        clearDroppedOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearIfSure(TimeTypes.DROPPEDOUT);
            }
        });

        Button clearNotStartedButton = view.findViewById(R.id.notStartedClearTimeButton);
        clearNotStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearIfSure(TimeTypes.DIDNOTSTART);
            }
        });

        RaceTimes timesToCheck = Objects.requireNonNull(checkpoints.getCurrentSelectedCheckpoint()).getRacerData(racer).getRaceTimes();
        enableOrDisableButtons(clearInButton, clearOutButton, clearDroppedOutButton, clearNotStartedButton, timesToCheck);

        Observer checkpointsObserver = new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                enableOrDisableButtons(clearInButton, clearOutButton, clearDroppedOutButton, clearNotStartedButton, timesToCheck);
            }
        };
        checkpointObservers.add(checkpointsObserver);
        checkpoints.addObserver(checkpointsObserver);

        Button doneButton = view.findViewById(R.id.finishedEditingButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    private void enableOrDisableButtons(Button clearInButton, Button clearOutButton, Button clearDroppedOutButton, Button clearNotStartedButton, RaceTimes timesToCheck) {
        clearInButton.setEnabled(shouldBeEnabled(timesToCheck, TimeTypes.IN));
        clearOutButton.setEnabled(shouldBeEnabled(timesToCheck, TimeTypes.OUT));
        clearDroppedOutButton.setEnabled(shouldBeEnabled(timesToCheck, TimeTypes.DROPPEDOUT));
        clearNotStartedButton.setEnabled(shouldBeEnabled(timesToCheck, TimeTypes.DIDNOTSTART));
    }

    private void clearIfSure(TimeTypes toCheck) {
        AlertDialog.Builder checkBuilder = new AlertDialog.Builder(getContext());
        String messageToShow = getString(R.string.are_you_sure_delete_the);
        switch (toCheck) {
            case IN:
                messageToShow = messageToShow + getString(R.string.checked_in_uncaps);
                break;
            case OUT:
                messageToShow = messageToShow + getString(R.string.checked_out_uncaps);
                break;
            case DROPPEDOUT:
                messageToShow = messageToShow + getString(R.string.dropped_out_uncaps);
                break;
            case DIDNOTSTART:
                messageToShow = messageToShow + getString(R.string.did_not_start_uncaps);
            default:
                Log.e("Error", "Attempting to clear a time type that is not handled");
        }
        messageToShow = messageToShow + " " + getString(R.string.time_question);
        if (checkpoints.getCurrentSelectedCheckpoint().getRacerData(racer).getReportedItems().getReportedItem(toCheck)) {
            messageToShow = messageToShow + " " + getString(R.string.reported_time_already);
        }
        checkBuilder.setMessage(messageToShow);
        checkBuilder.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Vibrate().pulse(getContext());
                checkpoints.clearRacerTime(checkpoints.getCurrentCheckpointNumber(), racer, toCheck);
                checkpoints.notifyObservers();
            }
        });
        checkBuilder.setPositiveButton(getString(R.string.cancel), null);
        checkBuilder.create().show();
    }

    private boolean shouldBeEnabled(RaceTimes timesToCheck, TimeTypes typeToCheck) {
        return timesToCheck.getTimeOfType(typeToCheck) != null;
        /*switch (typeToCheck) {
            case IN:
                return timesToCheck.getInTime() != null;
            case OUT:
                return timesToCheck.getOutTime() != null;
            case DIDNOTSTART:
                return timesToCheck.getDroppedOutTime() != null || timesToCheck.getOutTime() != null || timesToCheck.getInTime() != null;
            case DROPPEDOUT:
                return timesToCheck.getNotStartedTime() != null;
            default:
                Log.e("Error", "Attempted to find information about a time type that is not checked for. Returned False.");
                return false;
        }*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("checkpoints", checkpoints);
        outState.putParcelable("racer", racer);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        for (Observer observer : checkpointObservers) {
            checkpoints.deleteObserver(observer);
        }
        inModifyButton.removeObservers();
        outModifyButton.removeObservers();
        droppedOutModifyButton.removeObservers();
        notStartedModifyButton.removeObservers();
        super.onDismiss(dialog);
    }
}
